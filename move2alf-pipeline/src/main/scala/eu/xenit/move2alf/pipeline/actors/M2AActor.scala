package eu.xenit.move2alf.pipeline.actors

import akka.actor._
import eu.xenit.move2alf.pipeline._
import eu.xenit.move2alf.common.LogHelper
import eu.xenit.move2alf.pipeline.actions.context.AbstractActionContextFactory
import eu.xenit.move2alf.pipeline.M2AMessage
import eu.xenit.move2alf.pipeline.TaskMessage
import scala.Some
import akka.routing.Broadcast
import scala.collection.mutable

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
case class Negotiate(actors: Seq[ActorRef])
case class Flush(actors: Array[ActorRef])

sealed trait ActorData
case object Empty extends ActorData
case class AliveData(counter: Int, negotiateCounters: MultiDimensionalMap[ActorRef, Int], flushCounters: MultiDimensionalMap[ActorRef, Int]) extends ActorData


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 2/28/13
 * Time: 10:05 AM
 * To change this template use File | Settings | File Templates.
 */
class M2AActor(protected val factory: AbstractActionContextFactory, protected val nmbOfNonLoopedSenders: Int, protected val nmbOfLoopedSenders: Int) extends Actor with FSM[ActorState, ActorData] with LogHelper{

//  logger.debug("Creating actor: "+context.self+" with number of senders: "+nmbOfSenders)
  private val action = factory.createActionContext(context)

  startWith(Death, Empty)

  when(Death) {
    case Event(Start | Broadcast(Start), _) => {
      action.sendStartMessage()
      goto(Alive) using AliveData(counter = nmbOfNonLoopedSenders,negotiateCounters =  new MultiDimensionalMap[ActorRef, Int], flushCounters =  new MultiDimensionalMap[ActorRef, Int])
    }
  }

  private def stayOrDie(data: AliveData): M2AActor.this.type#State = {
    if (!action.blocked && data.counter == 0) {
      action.broadCastEOC()
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
    }
  }

  when(Alive) {
    case Event(EOC | Broadcast(EOC), data: AliveData) => {
      val count = data.counter -1
      if(count == 0){
        if(nmbOfLoopedSenders == 0){
          if(!action.blocked){
            action.broadCastEOC()
            goto(Death) using Empty
          } else {
            stay() using data.copy(counter = count)
          }
        } else {
          goto(Negotiating) using data.copy(counter = nmbOfLoopedSenders)
        }
      } else {
        stay() using data.copy(counter = count)
      }
    }
    case Event(Start | Broadcast(Start), _) => {
      stay
    }
    case Event(Negotiate(actors), data: AliveData) => {
      aliveNegotiate(data, actors)
    }
    case Event(Broadcast(Negotiate(actors)), data: AliveData) => {
      aliveNegotiate(data, actors)
    }
    case event => handleCommonMessages(event)
  }



  private def aliveNegotiate(data: AliveData, actors: Seq[ActorRef]): M2AActor.this.type#State = {
    val count = data.negotiateCounters.getValue(actors).getOrElse(0) + 1
    if (actors.last == context.self) {
      //self sent negotiation
      if (count == nmbOfLoopedSenders + nmbOfNonLoopedSenders) {
        //not only from normal senders
        action.broadCast(Negotiate(actors.slice(0, actors.size - 1))) //broadcast original message because succeeded negotiation
        data.negotiateCounters.putValue(actors, 0) //reset message counter
        data.negotiateCounters.putValue(actors.slice(0, actors.size - 1), -nmbOfLoopedSenders) //expect messages back from loops
        stay()
      } else {
        data.negotiateCounters.putValue(actors, count) //if not enough messages received, just augment counter
        stay()
      }
    } else if (actors.contains(context.self)) {
      if (count == nmbOfNonLoopedSenders) {
        //received enough
        action.broadCast(Negotiate(actors)) //broadcast the negotiation
        data.negotiateCounters.putValue(actors, -nmbOfLoopedSenders) //expect loop callbacks
        stay()
      } else {
        data.negotiateCounters.putValue(actors, count)
        stay()
      }
    } else if (count == nmbOfNonLoopedSenders) {
      //forward with extra negotiation
      if (nmbOfLoopedSenders > 0) {
        data.negotiateCounters.putValue(actors, 0)
        action.broadCast(Negotiate(actors.:+(context.self))) //add this negiotiation to list
        stay()
      } else {
        action.broadCast(Negotiate(actors))
        stay()
      }
    } else {
      data.negotiateCounters.putValue(actors, count)
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
    case event => handleCommonMessages(event)
  }

  when(Flushing) {
    case event => handleCommonMessages(event)
  }


  private def negotiation(actors: Seq[ActorRef], data: AliveData): M2AActor.this.type#State = {
    if (actors.size == 1 && actors.last == context.self) {
      val count = data.counter - 1
      if (count == 0) {
        goto(Flushing) using data.copy(counter = nmbOfLoopedSenders)
      } else {
        stay using data.copy(counter = count)
      }
    } else {
      aliveNegotiate(data, actors)
    }
  }

  onTransition {
    case Death -> Alive => {
      action.onStart()
    }
    case Alive -> Negotiating => {
      action.broadCast(Negotiate(Array(context.self)))
    }
  }
}
