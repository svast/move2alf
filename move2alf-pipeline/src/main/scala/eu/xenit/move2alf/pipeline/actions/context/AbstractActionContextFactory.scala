package eu.xenit.move2alf.pipeline.actions.context

import eu.xenit.move2alf.pipeline.actions.{EOCBlockingAction, HasSendingContext, HasStateContext}
import eu.xenit.move2alf.common.LogHelper
import akka.actor.ActorContext

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/2/13
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractActionContextFactory(val actionClass: Class[_], private val parameters: Map[String, AnyRef])(implicit val context: ActorContext) extends LogHelper{

  protected type T


  def createActionContext(): AbstractActionContext = {
    val basicAction: T = getAction

    val actionContext = constructActionContext(basicAction)

    basicAction match {
      case sa: HasStateContext => {
        logger.debug("Adding StateContext")
        sa.setStateContext(new StateContextImpl(actionContext))
      }
      case _ =>
    }

    basicAction match {
      case sa: EOCBlockingAction => {
        logger.debug("Adding EOCBlockingContext")
        sa.setEOCBlockingContext(new EOCBlockingContextImpl(actionContext))
      }
      case _ =>
    }

    actionContext
  }


  protected def constructActionContext(basicAction: T): AbstractActionContext with StateActionContext with EOCBlockingActionContext

  protected def addSendingContext(basicAction: T, context: SendingActionContext) {
    basicAction match {
      case sa: HasSendingContext => {
        logger.debug("Adding SendingContext")
        sa.setSendingContext(new SendingContextImpl(context))
      }
      case _ =>
    }
  }

  protected def getAction: T = {
    val constructor = actionClass.getConstructor()
    val basicAction: T = constructor.newInstance().asInstanceOf[T]

    parameters foreach {
      case (key, value) => actionClass.getMethod("set"+key.capitalize,classOf[String]).invoke(basicAction, value)
    }

    basicAction
  }

}
