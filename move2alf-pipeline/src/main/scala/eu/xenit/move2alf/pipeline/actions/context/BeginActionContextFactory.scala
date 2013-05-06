package eu.xenit.move2alf.pipeline.actions.context

import akka.actor.{ActorContext, ActorRef}
import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.actions.BeginAction

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/30/13
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */
class BeginActionContextFactory(actionClass: Class[_], parameters: Map[String, AnyRef], receivers: Map[String, ActorRef])(implicit context: ActorContext, jobContext: JobContext) extends AbstractActionContextFactory(actionClass, parameters) {
  protected type T = BeginAction

  protected def constructActionContext(basicAction: T) = {
    val actionContext = new AbstractActionContext(receivers, 1) with StateActionContext with StartActionContext with SendingActionContext with EOCBlockingActionContext{
      val action = basicAction
    }
    addSendingContext(basicAction, actionContext)
    actionContext
  }
}
