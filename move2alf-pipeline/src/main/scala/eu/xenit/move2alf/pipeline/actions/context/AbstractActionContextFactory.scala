package eu.xenit.move2alf.pipeline.actions.context

import eu.xenit.move2alf.pipeline.actions._
import eu.xenit.move2alf.common.LogHelper
import akka.actor.ActorContext

/**
 * User: Thijs Lemmens
 * Date: 5/2/13
 * Time: 3:36 PM
 */
abstract class AbstractActionContextFactory(id: String, actionFactory: ActionFactory) extends LogHelper{


  def createActionContext(context: ActorContext): AbstractActionContext = {
    val basicAction: Action = getAction

    val actionContext = constructActionContext(basicAction)(context)

    basicAction match {
      case sa: HasStateContext => {
//        logger.debug("Adding StateContext")
        sa.setStateContext(new StateContextImpl(actionContext))
      }
      case _ =>
    }

    basicAction match {
      case sa: EOCBlockingAction => {
//        logger.debug("Adding EOCBlockingContext")
        sa.setEOCBlockingContext(new EOCBlockingContextImpl(actionContext))
      }
      case _ =>
    }

    basicAction match {
      case sa: HasTaskContext => {
//        logger.debug("Adding TaskContext")
        sa.setTaskContext(new TaskContextImpl(actionContext))
      }
      case _ =>
    }

    actionContext
  }


  protected def constructActionContext(basicAction: Action)(implicit context: ActorContext): AbstractActionContext

  protected def addSendingContext(basicAction: Action, context: AbstractActionContext) {
    basicAction match {
      case sa: HasSendingContext => {
//        logger.debug("Adding SendingContext")
        sa.setSendingContext(new SendingContextImpl(context))
      }
      case _ =>
    }
  }

  protected def getAction: Action = {
    actionFactory.createAction()
  }

}
