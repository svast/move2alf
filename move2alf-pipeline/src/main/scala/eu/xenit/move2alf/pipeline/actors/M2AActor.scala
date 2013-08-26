package eu.xenit.move2alf.pipeline.actors

import akka.actor._
import eu.xenit.move2alf.pipeline._
import eu.xenit.move2alf.common.LogHelper
import eu.xenit.move2alf.pipeline.actions.context.AbstractActionContextFactory
import eu.xenit.move2alf.pipeline.M2AMessage
import eu.xenit.move2alf.pipeline.TaskMessage
import scala.Some
import akka.routing.Broadcast

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 2/28/13
 * Time: 10:05 AM
 * To change this template use File | Settings | File Templates.
 */
class M2AActor(protected val factory: AbstractActionContextFactory, protected val nmbOfSenders: Int) extends Actor with FSM[JobState, Data] with LogHelper{

//  logger.debug("Creating actor: "+context.self+" with number of senders: "+nmbOfSenders)
  private val action = factory.createActionContext(context)

  startWith(NotRunning, Uninitialized)

  when(NotRunning) {
    case Event(Start | Broadcast(Start), Uninitialized) => {
      action.sendStartMessage()
      goto(Running) using CycleData(counter = nmbOfSenders)
    }
  }

  when(Running) {
    case Event(EOC | Broadcast(EOC), data: CycleData) => {
//      logger.debug("Received EOC in "+context.self)
      stayOrStop(data, true)
    }
    case Event(Start | Broadcast(Start), _) => {
      stay
    }
    case Event(M2AMessage(message), data: CycleData) => {
      action.receive(message)
      stayOrStop(data, false)
    }
    case Event(TaskMessage(key, message, replyTo), data: CycleData) => {
      action.receive(message, Some(key), Some(replyTo))
      stayOrStop(data, false)
    }
    case Event(ReplyMessage(key, message), data: CycleData) => {
      action.receiveReply(key, message)
      stayOrStop(data, false)
    }
  }

  private def stayOrStop(data: CycleData, decrement: Boolean): FSM.State[JobState, Data] = {
    val count = if(decrement) data.counter-1 else data.counter
    count match {
      case 0 => if(action.blocked) stay using data.copy(counter = count) else {
        action.broadCastEOC()
        goto(NotRunning) using Uninitialized
      }
      case _ => stay using data.copy(counter = count)
    }
  }

  onTransition {
    case NotRunning -> Running => {
      action.onStart()
    }
  }
}
