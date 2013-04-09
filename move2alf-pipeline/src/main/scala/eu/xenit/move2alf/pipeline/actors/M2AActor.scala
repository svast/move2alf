package eu.xenit.move2alf.pipeline.actors

import akka.actor._
import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.{M2AMessage, AbstractMessage}
import eu.xenit.move2alf.pipeline.actions.SimpleAction

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 2/28/13
 * Time: 10:05 AM
 * To change this template use File | Settings | File Templates.
 */
class M2AActor[T <: AbstractMessage, U <: AbstractMessage](action: SimpleAction[T,U], receiver: ActorRef, nmbSenders: Int = 1)(implicit jobContext: JobContext) extends AbstractM2AActor(action, receiver, nmbSenders) with ReceivingActor[T] with SendingActor[U] with StateActor{

  override def receive = {
    case M2AMessage(message) => execute(message.asInstanceOf[T])
    case s => super.receive(s)
  }

  override protected def execute(message: T){
    action.execute(message, this)
  }

}
