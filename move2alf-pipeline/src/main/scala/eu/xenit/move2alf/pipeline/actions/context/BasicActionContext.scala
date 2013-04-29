package eu.xenit.move2alf.pipeline.actions.context

import eu.xenit.move2alf.pipeline.AbstractMessage
import akka.actor.ActorRef
import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.actions.{ReceivingAction}


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/7/13
 * Time: 9:58 AM
 * To change this template use File | Settings | File Templates.
 */
class BasicActionContext[T <: AbstractMessage, V <: AbstractMessage](private val action: ReceivingAction[T], receivers: Map[String, ActorRef], nmbSenders: Int)(implicit jobContext: JobContext) extends AbstractActionContext(receivers, nmbSenders) with ReceivingActionContext[T] with SendingActionContext with StateActionContext{

  def execute(message: T) {
    //action.execute(message)
  }
}
