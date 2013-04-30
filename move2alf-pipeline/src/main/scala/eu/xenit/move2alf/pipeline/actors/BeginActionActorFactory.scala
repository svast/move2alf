package eu.xenit.move2alf.pipeline.actors

import akka.actor.{ActorContext, ActorRef}
import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.actions.BeginAction
import eu.xenit.move2alf.pipeline.actions.context.BeginActionContext

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/30/13
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */
class BeginActionActorFactory(actionClass: String, parameters: Map[String, AnyRef], receivers: Map[String, ActorRef], nmbActors: Int = 1)(implicit context: ActorContext, jobContext: JobContext) extends AbstractActionActorFactory(actionClass, parameters, nmbActors) {
  protected type T = BeginAction
  protected type U = BeginActionContext

  protected def constructActionContext(basicAction: T): U = {
    val actionContext = new BeginActionContext(basicAction, receivers)
    addSendingContext(basicAction, actionContext)
    actionContext
  }
}
