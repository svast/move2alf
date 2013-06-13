package eu.xenit.move2alf.pipeline.actions.context

import akka.actor.{ActorContext, ActorRef}
import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.actions.{Action, ActionFactory, HasStateContext, ReceivingAction}

/**
 * User: Thijs Lemmens
 * Date: 4/30/13
 * Time: 9:55 AM
 */
class BasicActionContextFactory(id: String, actionFactory: ActionFactory, receivers: Map[String, ActorRef])(implicit jobContext: JobContext) extends AbstractActionContextFactory(id, actionFactory){

  protected def constructActionContext(basicAction: Action)(implicit context: ActorContext) = {
    val actionContext = new AbstractActionContext(id, receivers){
        val action = basicAction
     }
    addSendingContext(basicAction, actionContext)
    actionContext
  }

}
