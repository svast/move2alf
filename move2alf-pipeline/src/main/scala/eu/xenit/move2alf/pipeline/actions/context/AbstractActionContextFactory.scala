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
abstract class AbstractActionContextFactory(id: String, actionClass: Class[_], parameters: Map[String, AnyRef]) extends LogHelper{

  protected type T


  def createActionContext(context: ActorContext): AbstractActionContext = {
    val basicAction: T = getAction

    val actionContext = constructActionContext(basicAction)(context)

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


  protected def constructActionContext(basicAction: T)(implicit context: ActorContext): AbstractActionContext

  protected def addSendingContext(basicAction: T, context: AbstractActionContext) {
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
    val methods = actionClass.getMethods
    val methodMap = methods map {
      method => (method.getName, method)
    } toMap

    parameters foreach {
      case (key, value) => {
        try {
          val method = methodMap("set"+key.capitalize)

          try {
            logger.debug("Setting parameter: "+key+", value: "+value.toString);
            method.invoke(basicAction, value)
          } catch {
            case e: IllegalArgumentException => {
              logger.error("Could not set parameter: "+key+"\n" +
                "Method parameter: "+method.getParameterTypes()(0).getCanonicalName+"\n" +
                "Value type: "+value.getClass.getCanonicalName, e)
            }
            case e: NullPointerException => {
              logger.error("NullPointer", e);
              if(method == null){
                logger.error("method is null")
              }
              logger.error("key: "+key)
              logger.error("value: "+value)
            }
          }
        } catch {
          case e: NoSuchElementException => logger.info("No setter for parameter: "+key)
        }
      }
    }
    basicAction
  }

}
