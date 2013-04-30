package eu.xenit.move2alf.pipeline.actions.context

import eu.xenit.move2alf.pipeline.AbstractMessage
import akka.actor.ActorRef
import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.actions.ReceivingAction

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/25/13
 * Time: 12:02 PM
 * To change this template use File | Settings | File Templates.
 */
class EndActionContext[T <: AbstractMessage](private val action: ReceivingAction[T], receiver: (String, ActorRef), nmbSenders: Int)(implicit jobContext: JobContext) extends AbstractActionContext(Map(receiver), nmbSenders) with ReceivingActionContext[T] with StateActionContext {
  protected def execute(message: T) {
    action.execute(message: T)
  }
}
