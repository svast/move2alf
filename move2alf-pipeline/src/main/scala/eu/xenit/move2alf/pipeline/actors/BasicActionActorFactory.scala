package eu.xenit.move2alf.pipeline.actors

import java.lang.reflect.{ParameterizedType, Type}
import scala.Function._
import akka.actor._
import eu.xenit.move2alf.pipeline.AbstractMessage
import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.actions.context.{StateContextImpl, SendingContextImpl, BasicActionContext}
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
class BasicActionActorFactory(private val actionClass: String, private val parameters: Map[String, String], receiver: ActorRef, private val nmbOfSenders: Int, private val nmbActors: Int = 1)(implicit context: ActorContext, jobContext: JobContext) extends AbstractActorFactory with LogHelper{


  def createActor: ActorRef = {
    val subClass = Class.forName(actionClass)
    val constructor = subClass.getConstructor()
    val basicAction: ReceivingAction[AbstractMessage] = constructor.newInstance().asInstanceOf[ReceivingAction[AbstractMessage]]

    parameters foreach {
      case (key, value) => subClass.getMethod("set"+key.capitalize,classOf[String]).invoke(basicAction, value)
    }
    val wrapper: BasicActionContext[AbstractMessage, AbstractMessage] = new BasicActionContext[AbstractMessage, AbstractMessage](basicAction, Map("default" -> receiver), nmbOfSenders)

    basicAction match {
      case sa: HasSendingContext => {
        logger.debug("Adding SendingContext")
        sa.setSendingContext(new SendingContextImpl(wrapper))
      }
    }

    basicAction match {
      case sa: HasStateContext => {
        logger.debug("Adding StateContext")
        sa.setStateContext(new StateContextImpl(wrapper))
      }
    }

    if(nmbActors == 1){
      return context.actorOf(Props(new M2AActor(wrapper)))
    } else {
      return context.actorOf(Props(new M2AActor(wrapper)).withRouter(SmallestMailboxRouter(nmbActors)))
    }
  }
}
