package eu.xenit.move2alf.pipeline.actors

import java.lang.reflect.{ParameterizedType, Type}
import scala.Function._
import akka.actor._
import eu.xenit.move2alf.pipeline.AbstractMessage
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
abstract class AbstractActionActorFactory(private val actionClass: String, private val parameters: Map[String, AnyRef], private val nmbActors: Int = 1)(implicit context: ActorContext, jobContext: JobContext) extends AbstractActorFactory with LogHelper{

  protected type T
  protected type U <: AbstractActionContext with StateActionContext

  def createActor: ActorRef = {

    val wrapper: U = getActionContext()

    if(nmbActors == 1){
      return context.actorOf(Props(new M2AActor(wrapper)))
    } else {
      return context.actorOf(Props(new M2AActor(wrapper)).withRouter(SmallestMailboxRouter(nmbActors)))
    }
  }


  protected def getActionContext(): U = {
    val basicAction: T = getAction

    val actionContext = constructActionContext(basicAction)

    basicAction match {
      case sa: HasStateContext => {
        logger.debug("Adding StateContext")
        sa.setStateContext(new StateContextImpl(actionContext))
      }
    }

    actionContext
  }


  protected def constructActionContext(basicAction: T): U

  protected def addSendingContext(basicAction: T, context: SendingActionContext) {
    basicAction match {
      case sa: HasSendingContext => {
        logger.debug("Adding SendingContext")
        sa.setSendingContext(new SendingContextImpl(context))
      }
    }
  }

  protected def getAction: T = {
    val subClass = Class.forName(actionClass)
    val constructor = subClass.getConstructor()
    val basicAction: T = constructor.newInstance().asInstanceOf[T]

    parameters foreach {
      case (key, value) => subClass.getMethod("set"+key.capitalize,classOf[String]).invoke(basicAction, value)
    }

    basicAction
  }
}
