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
class M2AActor[T <: AbstractMessage, U <: AbstractMessage](receiver: ActorRef, private val action: SimpleAction[T,U], nmbSenders: Int = 1)(implicit jobContext: JobContext) extends PipelineActor(receiver, nmbSenders) with StateActor{

  override def receive = {
    case M2AMessage(message) => execute(message.asInstanceOf[T])
    case s => super.receive(s)
  }

  protected def execute(message: T){
    action.execute(message, this)
  }

  def sendMessage(message: U) = {
    receiver ! M2AMessage(message)
  }

}
