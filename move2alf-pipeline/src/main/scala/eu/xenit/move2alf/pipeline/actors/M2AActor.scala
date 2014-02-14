package eu.xenit.move2alf.pipeline.actors

import akka.actor._
import eu.xenit.move2alf.pipeline._
import eu.xenit.move2alf.common.LogHelper
import eu.xenit.move2alf.pipeline.actions.context.AbstractActionContextFactory
import scala.collection.mutable
import eu.xenit.move2alf.pipeline.M2AMessage
import eu.xenit.move2alf.pipeline.TaskMessage
import scala.Some
import akka.routing.Broadcast
import eu.xenit.move2alf.pipeline.ReplyMessage

class MultiDimensionalMap[K, V] {
  var value: V = _
  val map: mutable.Map[K, MultiDimensionalMap[K,V]] = new mutable.HashMap[K,MultiDimensionalMap[K,V]]()

  def putValue(keys: Seq[K], value: V){
    if(keys.size == 0){
      this.value = value
    } else {
      map.get(keys.head).getOrElse({
        val multiMap =  new MultiDimensionalMap[K,V]
        map.put(keys.head, multiMap)
        multiMap
      }).putValue(keys.tail, value)
    }
  }

  def getValue(keys: Seq[K]): Option[V] = {
    if(keys.size == 0){
      Some(this.value)
    } else {
      if(map.contains(keys.head)){
        map.get(keys.head).get.getValue(keys.tail)
      } else {
        None
      }
    }
  }

  def getValue(key: K): V = {
    this.value
  }
}

sealed trait ActorState
case object Death extends ActorState
case object Alive extends ActorState
case object Negotiating extends ActorState
case object Flushing extends ActorState
case object NearDeath extends ActorState

sealed trait ActorMessage
case class Negotiate(actors: Seq[ActorRef]) extends ActorMessage
case class Flush(actors: Seq[ActorRef]) extends ActorMessage
case object ReadyToDie extends ActorMessage
case object BackAlive extends ActorMessage

sealed trait ActorData
case object Empty extends ActorData
case class AliveData(nmbOfEOC: Int, nmbOfReadyToDie: Int, counter: Int, negotiateCounters: MultiDimensionalMap[ActorRef, Int], flushCounters: MultiDimensionalMap[ActorRef, Int]) extends ActorData


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 2/28/13
 * Time: 10:05 AM
 * To change this template use File | Settings | File Templates.
 */
class M2AActor(protected val factory: AbstractActionContextFactory, protected val nmbOfNonLoopedSenders: Int, protected val nmbOfLoopedSenders: Int, val actionIdToNumberOfSenders: String => Int) extends Actor with FSM[ActorState, ActorData] with LogHelper{

//  logger.debug("Creating actor: "+context.self+" with number of senders: "+nmbOfSenders)
  private val action = factory.createActionContext(context)

  startWith(Death, Empty)

  when(Death) {
    case Event(Start | Broadcast(Start), _) => {
      action.sendStartMessage()
      goto(Alive) using AliveData(nmbOfEOC = 0, nmbOfReadyToDie = 0, counter = 0, negotiateCounters =  new MultiDimensionalMap[ActorRef, Int], flushCounters =  new MultiDimensionalMap[ActorRef, Int])
    }
  }

  private def stayOrDie(data: AliveData): M2AActor.this.type#State = {
    if (!action.blocked && data.nmbOfEOC == nmbOfNonLoopedSenders) {
      action.flush()
      action.broadCast(EOC)
      goto(Death) using Empty
    } else {
      stay()
    }
  }

  private def handleCommonMessages(event: M2AActor.this.type#Event): M2AActor.this.type#State = {
    event match  {
      case Event(M2AMessage(message), data: AliveData) => {
        action.receive(message)
        stayOrDie(data)
      }
      case Event(TaskMessage(key, message, replyTo), data: AliveData) => {
        action.receive(message, Some(key), Some(replyTo))
        stayOrDie(data)
      }
      case Event(ReplyMessage(key, message), data: AliveData) => {
        action.receiveReply(key, message)
        stayOrDie(data)
      }
      case Event(BackAlive | Broadcast(BackAlive), data: AliveData) => {
        stay() using data.copy(nmbOfReadyToDie = data.nmbOfReadyToDie - 1)
      }
    }
  }

  when(Alive) {
    case Event(EOC | Broadcast(EOC), data: AliveData) => {
      val count = data.nmbOfEOC +1
      if(count == nmbOfNonLoopedSenders){
        if(nmbOfLoopedSenders == 0){
          if(!action.blocked){
            action.flush()
            action.broadCast(EOC)
            goto(Death) using Empty
          } else {
            stay() using data.copy(nmbOfEOC = count)
          }
        } else {
          goto(Negotiating) using data.copy(nmbOfEOC = count, counter = nmbOfLoopedSenders)
        }
      } else {
        stay() using data.copy(nmbOfEOC = count)
      }
    }
    case Event(Start | Broadcast(Start), _) => {
      stay
    }
    case Event(Negotiate(actors), data: AliveData) => {
      aliveNegotiate(data, actors)
    }
    case Event(Broadcast(Negotiate(actors)), data:AliveData) => {
      aliveNegotiate(data, actors)
    }
    case Event(Flush(actors), data: AliveData) => {
      aliveFlushNegotiate(data, actors)
    }
    case Event(Broadcast(Flush(actors)), data:AliveData) => {
      aliveNegotiate(data, actors)
    }
    case Event(ReadyToDie | Broadcast(ReadyToDie), data: AliveData) => {
      val count = data.nmbOfReadyToDie + 1
      if(nmbOfLoopedSenders == 0 && count == nmbOfNonLoopedSenders){
        goto(NearDeath) using data.copy(nmbOfReadyToDie = count)
      } else if(nmbOfNonLoopedSenders > 0 && count == nmbOfNonLoopedSenders){
        goto(Negotiating) using data.copy(nmbOfReadyToDie = count)
      } else {
        stay() using data.copy(nmbOfReadyToDie = count)
      }
    }
    case event => handleCommonMessages(event)
  }



  private def aliveNegotiate(data: AliveData, actors: Seq[ActorRef]): State = {
    aliveNegotiateOrFlush(actors, data.negotiateCounters, outActors => action.broadCast(Negotiate(outActors)))
  }

  private def aliveFlushNegotiate(data: AliveData, actors: Seq[ActorRef]): State = {
    aliveNegotiateOrFlush(actors, data.flushCounters, outActors => {
      action.flush()
      action.broadCast(Flush(outActors))
    })
  }


  def aliveNegotiateOrFlush(actors: Seq[ActorRef], counters: MultiDimensionalMap[ActorRef, Int], broadCastFunction: Seq[ActorRef] => Unit): M2AActor.this.type#State = {
    val count = counters.getValue(actors).getOrElse(0) + 1
    if (actors.last == context.self) {
      //self sent negotiation
      if (count == nmbOfLoopedSenders + nmbOfNonLoopedSenders) {
        //not only from normal senders
        broadCastFunction(actors.slice(0, actors.size - 1)) //broadcast original message because succeeded negotiation
        counters.putValue(actors, 0) //reset message counter
        counters.putValue(actors.slice(0, actors.size - 1), -nmbOfLoopedSenders) //expect messages back from loops
        stay()
      } else {
        counters.putValue(actors, count) //if not enough messages received, just augment counter
        stay()
      }
    } else if (actors.contains(context.self)) {
      if (count == nmbOfNonLoopedSenders) {
        //received enough
        broadCastFunction(actors) //broadcast the negotiation
        counters.putValue(actors, -nmbOfLoopedSenders) //expect loop returns
        stay()
      } else {
        counters.putValue(actors, count)
        stay()
      }
    } else if (count == nmbOfNonLoopedSenders) {
      //forward with extra negotiation
      if (nmbOfLoopedSenders > 0) {
        counters.putValue(actors, 0)
        broadCastFunction(actors.:+(context.self)) //add this negiotiation to list
        stay()
      } else {
        broadCastFunction(actors)
        stay()
      }
    } else {
      counters.putValue(actors, count)
      stay()
    }
  }

  when(Negotiating) {
    case Event(Negotiate(actors), data: AliveData) => {
      negotiation(actors, data)
    }
    case Event(Broadcast(Negotiate(actors)), data: AliveData) => {
      negotiation(actors, data)
    }
    case Event(Flush(actors), data: AliveData) => {
      aliveFlushNegotiate(data, actors)
    }
    case Event(Broadcast(Flush(actors)), data:AliveData) => {
      aliveNegotiate(data, actors)
    }
    case Event(ReadyToDie | Broadcast(ReadyToDie), data:AliveData) => {
      stay() using data.copy(nmbOfReadyToDie = data.nmbOfReadyToDie+1)
    }
    case event => handleCommonMessages(event)
  }

  private def flushing(actors: Seq[ActorRef], data: AliveData): M2AActor.this.type#State = {
    negotiationOrFlushing(actors, data, Unit => goto(NearDeath) using data.copy(counter = nmbOfLoopedSenders), Unit => aliveFlushNegotiate(data, actors))
  }


  when(Flushing) {
    case Event(Flush(actors), data: AliveData) => {
      flushing(actors, data)
    }
    case Event(Broadcast(Flush(actors)), data: AliveData) => {
      flushing(actors,data)
    }
    case Event(Negotiate(actors), data: AliveData) => {
      aliveNegotiate(data, actors)
    }
    case Event(Broadcast(Negotiate(actors)), data:AliveData) => {
      aliveNegotiate(data, actors)
    }
    case Event(ReadyToDie | Broadcast(ReadyToDie), data:AliveData) => {
      stay() using data.copy(nmbOfReadyToDie = data.nmbOfReadyToDie+1)
    }
    case event => handleCommonMessages(event)
  }

  when(NearDeath){
    case Event(EOC | Broadcast(EOC), data: AliveData) => {
      val eocCount = data.nmbOfEOC + 1
      val readyToDieCount = data.nmbOfReadyToDie
      stayNearlyDeadOrDie(eocCount, readyToDieCount, data)
    }
    case Event(ReadyToDie | Broadcast(ReadyToDie), data: AliveData) => {
      stayNearlyDeadOrDie(data.nmbOfEOC, data.nmbOfReadyToDie + 1, data)
    }
    case Event(BackAlive | Broadcast(BackAlive), data: AliveData) => {
      stay() using data.copy(nmbOfReadyToDie = data.nmbOfReadyToDie+1)
    }
    case Event(M2AMessage(message), data: AliveData) => {
      action.receive(message)
      handleNormalMessagesNearlyDead(data)
    }
    case Event(TaskMessage(key, message, replyTo), data: AliveData) => {
      action.receive(message, Some(key), Some(replyTo))
      handleNormalMessagesNearlyDead(data)
    }
    case Event(ReplyMessage(key, message), data: AliveData) => {
      action.receiveReply(key, message)
      handleNormalMessagesNearlyDead(data)
    }
  }


  private def handleNormalMessagesNearlyDead(data: AliveData): FSM.State[ActorState, ActorData] = {
    if (action.messageSent) {
      action.messageSent = false
      goto(Negotiating) using data
    } else {
      stayNearlyDeadOrDie(data.nmbOfEOC, data.nmbOfReadyToDie, data)
    }
  }

  def stayNearlyDeadOrDie(eocCount: Int, readyToDieCount: Int, data: AliveData): FSM.State[ActorState, ActorData] = {
    if(eocCount == nmbOfNonLoopedSenders && nmbOfLoopedSenders == 0){
      goto(Death) using Empty
    } else if (eocCount == nmbOfNonLoopedSenders && readyToDieCount == nmbOfNonLoopedSenders) {
      if (!action.blocked) {
        goto(Death) using Empty
      } else {
        stay using data.copy(nmbOfEOC = eocCount, nmbOfReadyToDie = readyToDieCount)
      }
    } else {
      stay using data.copy(nmbOfEOC = eocCount, nmbOfReadyToDie = readyToDieCount)
    }
  }

  private def shouldGoAlive(data: AliveData): Boolean = {
    if(data.nmbOfEOC + data.nmbOfReadyToDie == nmbOfNonLoopedSenders){
      false
    } else {
      true
    }
  }

  private def negotiation(actors: Seq[ActorRef], data: AliveData): M2AActor.this.type#State = {
    negotiationOrFlushing(actors, data, Unit => goto(Flushing) using data.copy(counter = nmbOfLoopedSenders), Unit =>  aliveNegotiate(data, actors))
  }


  def negotiationOrFlushing(actors: Seq[ActorRef], data: AliveData, succeededAction: Unit => State, defaultAction: Unit => State): M2AActor.this.type#State = {
    if (actors.size == 1 && actors.last == context.self) {
      val count = data.counter - 1
      if (count == 0) {
        if (action.messageSent) {
          action.messageSent = false
          //Negotiation failed, redo
          if(shouldGoAlive(data)){
            goto(Alive)
          } else {
            action.broadCast(Negotiate(Seq(context.self)))
            goto(Negotiating) using data.copy(counter = nmbOfLoopedSenders)
          }
        } else {
          if(shouldGoAlive(data)){
            goto(Alive)
          } else {
            succeededAction()
          }
        }
      } else {
        stay using data.copy(counter = count)
      }
    } else {
      defaultAction()
    }
  }

  onTransition {
    case Death -> Alive => {
      action.onStart()
    }
    case Alive -> Negotiating => {
      action.broadCast(Negotiate(Seq(context.self)))
    }
    case Negotiating -> Flushing => {
      action.flush()
      action.broadCast(Flush(Seq(context.self)))
    }
    case Flushing -> NearDeath => {
      action.broadCast(ReadyToDie)
    }
    case NearDeath -> Death => {
      action.broadCast(EOC)
    }
    case NearDeath -> _ => {
      action.broadCast(BackAlive)
    }
  }
}
