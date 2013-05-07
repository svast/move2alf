package eu.xenit.move2alf.pipeline.actors

import java.lang.reflect.{ParameterizedType, Type}
import akka.actor._
import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.actions.context._
import eu.xenit.move2alf.pipeline.actions.{HasStateContext, HasSendingContext, ReceivingAction}
import eu.xenit.move2alf.common.LogHelper
import akka.routing.SmallestMailboxRouter

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/10/13
 * Time: 2:25 PM
 * To change this template use File | Settings | File Templates.
 */
class ActionActorFactory(val id: String, val actionContextFactory: AbstractActionContextFactory, val nmbActors: Int = 1)(implicit context: ActorContext, jobContext: JobContext) extends AbstractActorFactory with LogHelper{

  def createActor: ActorRef = {
    return context.actorOf(Props(new M2AActor(actionContextFactory)).withRouter(SmallestMailboxRouter(nmbActors)), name = id)
  }

}
