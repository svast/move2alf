package eu.xenit.move2alf.pipeline.actors

import akka.actor._
import eu.xenit.move2alf.pipeline._
import eu.xenit.move2alf.common.LogHelper
import eu.xenit.move2alf.pipeline.actions.context.AbstractActionContextFactory
import eu.xenit.move2alf.pipeline.M2AMessage
import eu.xenit.move2alf.pipeline.TaskMessage
import scala.Some
import akka.routing.Broadcast
import eu.xenit.move2alf.pipeline.ReplyMessage

sealed trait ActorState
case object Death extends ActorState
case object Alive extends ActorState
case object Negotiating extends ActorState
case object Flushing extends ActorState
case object NearDeath extends ActorState

sealed trait ActorMessage
case class Negotiate(actors: Seq[(String, ActorRef)]) extends ActorMessage
case class Flush(actors: Seq[(String, ActorRef)]) extends ActorMessage
case object ReadyToDie extends ActorMessage
case object BackAlive extends ActorMessage

sealed trait ActorData
case object Empty extends ActorData
case class AliveData(nmbOfEOC: Int, nmbOfReadyToDie: Int, counter: Int, negotiateCounters: Map[Seq[(String,ActorRef)], Int], flushCounters: Map[Seq[(String,ActorRef)], Int]) extends ActorData


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 2/28/13
 * Time: 10:05 AM
 * To change this template use File | Settings | File Templates.
 */
class M2AActor(protected val factory: AbstractActionContextFactory, protected val nmbOfNonLoopedSenders: Int, protected val nmbOfLoopedSenders: Int, val actionIdToNumberOfSenders: String => Int, val nmbOfWorkers: Int = 1) extends Actor with FSM[ActorState, ActorData] with LogHelper{

//  logger.debug("Creating actor: "+context.self+" with number of senders: "+nmbOfSenders)
  private val action = factory.createActionContext(context)

  startWith(Death, Empty)

  when(Death) {
    case Event(Start | Broadcast(Start), _) => {
      goto(Alive) using AliveData(nmbOfEOC = 0, nmbOfReadyToDie = 0, counter = 0, negotiateCounters =  Map(), flushCounters =  Map())
    }
    case Event(BackAlive | Broadcast(BackAlive) | EOC | Broadcast(EOC), _) => {
      stay()
    }
    case Event(e, _) => {
      logger.error("Unexpected event {} in state Death for actionId {} and actorRef {}", e, action.id, context.self)
      assert(false)
      stay()
    }
  }

  private def stayOrDie(data: AliveData): M2AActor.this.type#State = {
    if (!action.blocked && data.nmbOfEOC == nmbOfNonLoopedSenders) {
      if(nmbOfLoopedSenders == 0){
        action.flush()
        action.broadCast(EOC)
        goto(Death) using Empty
      } else {
        goto(Negotiating) using data.copy(counter = nmbOfLoopedSenders*nmbOfWorkers)
      }
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
      case Event(Start | Broadcast(Start), _) => {
        stay()
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
          goto(Negotiating) using data.copy(nmbOfEOC = count, counter = nmbOfLoopedSenders*nmbOfWorkers)
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
        goto(Negotiating) using data.copy(nmbOfReadyToDie = count, counter = nmbOfLoopedSenders * nmbOfWorkers)
      } else {
        stay() using data.copy(nmbOfReadyToDie = count)
      }
    }
    case event => handleCommonMessages(event)
  }



  private def aliveNegotiate(data: AliveData, actors:  Seq[(String,ActorRef)]): State = {
    aliveNegotiateOrFlush(data, actors, data.negotiateCounters, outActors => action.broadCast(Negotiate(outActors)), counters => data.copy(negotiateCounters = counters))
  }

  private def aliveFlushNegotiate(data: AliveData, actors:  Seq[(String,ActorRef)]): State = {
    aliveNegotiateOrFlush(data, actors, data.flushCounters, outActors => {
      action.flush()
      action.broadCast(Flush(outActors))
    }, counters  => data.copy(flushCounters = counters))
  }


  def aliveNegotiateOrFlush(data: AliveData, actors: Seq[(String,ActorRef)], counters: Map[Seq[(String,ActorRef)], Int], broadCastFunction: Seq[(String, ActorRef)] => Unit, stateFunction: Map[Seq[(String, ActorRef)], Int] => ActorData): M2AActor.this.type#State = {
    val count = counters.get(actors).getOrElse(0) + 1
    if (actors.last == (action.id, context.self)) {
      //self sent negotiation
      if (count == nmbOfLoopedSenders + nmbOfNonLoopedSenders - data.nmbOfEOC) {
        //not only from normal senders
        val newMessageBody = actors.slice(0, actors.size - 1)
        broadCastFunction(newMessageBody) //broadcast original message because succeeded negotiation
        stay() using stateFunction(counters - actors + (newMessageBody -> -nmbOfLoopedSenders))
      } else {
        stay() using stateFunction(counters + (actors -> count))
      }
    } else if (messageContainsThisAction(actors)) {
      if (count == actionIdToNumberOfSenders(actors.last._1)) {
        //received enough
        broadCastFunction(actors) //broadcast the negotiation
        stay() using stateFunction(counters + (actors -> -nmbOfLoopedSenders))
      } else {
        stay() using stateFunction(counters + (actors -> count))
      }
    } else if (count == actionIdToNumberOfSenders(actors.last._1)) {
      //forward with extra negotiation
      if (nmbOfLoopedSenders > 0) {
        broadCastFunction(actors.:+(action.id, context.self)) //add this negiotiation to list
        stay() using stateFunction(counters - actors)
      } else {
        broadCastFunction(actors)
        stay()
      }
    } else {
      stay() using stateFunction(counters + (actors -> count))
    }
  }

  def messageContainsThisAction(actors: Seq[(String, ActorRef)]): Boolean = {
    actors.exists( tuple => tuple._1==action.id)
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
    case Event(M2AMessage(message), data: AliveData) => {
      action.receive(message)
      stay()
    }
    case Event(TaskMessage(key, message, replyTo), data: AliveData) => {
      action.receive(message, Some(key), Some(replyTo))
      stay()
    }
    case Event(ReplyMessage(key, message), data: AliveData) => {
      action.receiveReply(key, message)
      stay()
    }
    case event => handleCommonMessages(event)
  }

  private def flushing(actors: Seq[(String, ActorRef)], data: AliveData): M2AActor.this.type#State = {
    negotiationOrFlushing(actors, data, Unit => goto(NearDeath) using data.copy(counter = nmbOfLoopedSenders*nmbOfWorkers), Unit => aliveFlushNegotiate(data, actors))
  }


  when(Flushing) {
    case Event(Flush(actors), data: AliveData) => {
      flushing(actors, data)
    }
    case Event(Broadcast(Flush(actors)), data: AliveData) => {
      flushing(actors,data)
    }
    case Event(Negotiate(actors), data: AliveData) => {
      sameIdNegotiateWhenFlushing(actors, data)
    }
    case Event(Broadcast(Negotiate(actors)), data:AliveData) => {
      sameIdNegotiateWhenFlushing(actors, data)
    }
    case Event(ReadyToDie | Broadcast(ReadyToDie), data:AliveData) => {
      stay() using data.copy(nmbOfReadyToDie = data.nmbOfReadyToDie+1)
    }
    case Event(M2AMessage(message), data: AliveData) => {
      action.receive(message)
      stay()
    }
    case Event(TaskMessage(key, message, replyTo), data: AliveData) => {
      action.receive(message, Some(key), Some(replyTo))
      stay()
    }
    case Event(ReplyMessage(key, message), data: AliveData) => {
      action.receiveReply(key, message)
      stay()
    }
    case event => handleCommonMessages(event)
  }


  def sameIdNegotiateWhenFlushing(actors: Seq[(String, ActorRef)], data: AliveData): M2AActor.this.type#State = {
    if (actors.head._1 == action.id) {
      goto(Negotiating) using data.copy(counter = nmbOfWorkers * nmbOfLoopedSenders - 1)
    } else {
      aliveNegotiate(data, actors)
    }
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
      stay() using data.copy(nmbOfReadyToDie = data.nmbOfReadyToDie-1)
    }
    case Event(M2AMessage(message), data: AliveData) => {
      action.receive(message)
      handleNormalMessagesNearlyDead(data)
    }
    case Event(Flush(actors), data:AliveData) => {
      sameIdFlushWhenNearlyDead(actors, data)
    }
    case Event(Broadcast(Flush(actors)), data:AliveData) => {
      sameIdFlushWhenNearlyDead(actors,data)
    }
    case Event(TaskMessage(key, message, replyTo), data: AliveData) => {
      action.receive(message, Some(key), Some(replyTo))
      handleNormalMessagesNearlyDead(data)
    }
    case Event(ReplyMessage(key, message), data: AliveData) => {
      action.receiveReply(key, message)
      handleNormalMessagesNearlyDead(data)
    }
    case Event(Negotiate(_) | Broadcast(Negotiate(_)), data: AliveData) => {
      stay()
    }

  }


  def sameIdFlushWhenNearlyDead(actors: Seq[(String, ActorRef)], data: AliveData): M2AActor.this.type#State = {
    if (actors.head._1 == action.id) {
      goto(Flushing) using data.copy(counter = nmbOfWorkers * nmbOfLoopedSenders - 1)
    } else {
      stay()
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
      checkBlockedAndStayOrDie(data, eocCount, readyToDieCount)
    } else if (eocCount + readyToDieCount == nmbOfNonLoopedSenders + nmbOfLoopedSenders) {
      checkBlockedAndStayOrDie(data, eocCount, readyToDieCount)
    } else {
      stay using data.copy(nmbOfEOC = eocCount, nmbOfReadyToDie = readyToDieCount)
    }
  }


  def checkBlockedAndStayOrDie(data: AliveData, eocCount: Int, readyToDieCount: Int): FSM.State[ActorState, ActorData] = {
    if (!action.blocked) {
      goto(Death) using Empty
    } else {
      stay using data.copy(nmbOfEOC = eocCount, nmbOfReadyToDie = readyToDieCount)
    }
  }

  private def shouldGoAlive(data: AliveData): Boolean = {
    if(data.nmbOfEOC + data.nmbOfReadyToDie == nmbOfNonLoopedSenders && !action.blocked){
      false
    } else {
      true
    }
  }

  private def negotiation(actors: Seq[(String,ActorRef)], data: AliveData): M2AActor.this.type#State = {
    negotiationOrFlushing(actors, data, Unit => goto(Flushing) using data.copy(counter = nmbOfLoopedSenders*nmbOfWorkers), Unit =>  aliveNegotiate(data, actors))
  }


  def negotiationOrFlushing(actors: Seq[(String, ActorRef)], data: AliveData, succeededAction: Unit => State, defaultAction: Unit => State): M2AActor.this.type#State = {
    if (actors.size == 1 && actors.last._1 == action.id) {
      val count = data.counter - 1
      if (count == 0) {
        if (action.messageSent) {
          action.messageSent = false
          //Negotiation failed, redo
          if(shouldGoAlive(data)){
            goto(Alive)
          } else {
            action.broadCast(Negotiate(Seq((action.id, context.self))))
            goto(Negotiating) using data.copy(counter = nmbOfLoopedSenders*nmbOfWorkers)
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
      logger.debug(context.self +" is going from Death to Alive")
      action.broadCast(Start)
      action.onStart()
    }
    case Alive -> Negotiating => {
      logger.debug(context.self +" is going from Alive to Negotiating")
      action.broadCast(Negotiate(Seq((action.id, context.self))))
    }
    case Alive -> NearDeath => {
      logger.debug(context.self +" is going from Alive to NearDeath")
      action.broadCast(ReadyToDie)
    }
    case Negotiating -> Flushing => {
      logger.debug(context.self +" is going from Negotiating to Flushing")
      action.flush()
      action.broadCast(Flush(Seq((action.id, context.self))))
    }
    case Flushing -> NearDeath => {
      logger.debug(context.self +" is going from Flushing to NearDeath")
      action.broadCast(ReadyToDie)
    }
    case NearDeath -> Death => {
      logger.debug(context.self +" is going from NearDeath to Death")
      action.broadCast(BackAlive)
      action.broadCast(EOC)
    }
    case NearDeath -> b => {
      logger.debug(context.self +" is going from NearDeath to "+b)
      action.broadCast(BackAlive)
    }
    case a -> b => {
      logger.debug(context.self +" is going from "+a+" to "+b)
    }
  }
}
