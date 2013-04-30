package eu.xenit.move2alf.pipeline.actions.context

import akka.actor.ActorRef
import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.actions.BeginAction

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/25/13
 * Time: 10:27 AM
 * To change this template use File | Settings | File Templates.
 */
class BeginActionContext(private val action: BeginAction, receivers: Map[String, ActorRef])(implicit jobContext: JobContext) extends AbstractActionContext(receivers, 1) with StartActionContext with StateActionContext{
  protected def execute() {
    action.executeImpl(new SendingContextImpl(this))
  }
}
