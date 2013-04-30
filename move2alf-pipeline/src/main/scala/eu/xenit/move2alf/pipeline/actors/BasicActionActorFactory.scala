package eu.xenit.move2alf.pipeline.actors

import akka.actor.{ActorContext, ActorRef}
import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.actions.{HasStateContext, ReceivingAction}
import eu.xenit.move2alf.pipeline.AbstractMessage
import eu.xenit.move2alf.pipeline.actions.context.BasicActionContext

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/30/13
 * Time: 9:55 AM
 * To change this template use File | Settings | File Templates.
 */
class BasicActionActorFactory(actionClass: String, parameters: Map[String, AnyRef], receivers: Map[String, ActorRef], nmbOfSenders: Int, nmbActors: Int = 1)(implicit context: ActorContext, jobContext: JobContext) extends AbstractActionActorFactory(actionClass, parameters, nmbActors){
  protected type T = ReceivingAction[AbstractMessage]
  protected type U = BasicActionContext[AbstractMessage]

  protected def constructActionContext(basicAction: T): U = {
    val actionContext = new U(basicAction, receivers, nmbOfSenders)
    addSendingContext(basicAction, actionContext)
    actionContext
  }

}
