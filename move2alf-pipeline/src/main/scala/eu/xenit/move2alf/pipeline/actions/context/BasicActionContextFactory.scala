package eu.xenit.move2alf.pipeline.actions.context

import akka.actor.{ActorContext, ActorRef}
import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.actions.{HasStateContext, ReceivingAction}

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/30/13
 * Time: 9:55 AM
 * To change this template use File | Settings | File Templates.
 */
class BasicActionContextFactory(id: String, actionClass: Class[_], parameters: Map[String, AnyRef], receivers: Map[String, ActorRef])(implicit jobContext: JobContext) extends AbstractActionContextFactory(id, actionClass, parameters){
  protected type T = AnyRef

  protected def constructActionContext(basicAction: T)(implicit context: ActorContext) = {
    val actionContext = new AbstractActionContext(id, receivers){
        val action = basicAction
     }
    addSendingContext(basicAction, actionContext)
    actionContext
  }

}
