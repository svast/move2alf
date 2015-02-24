package eu.xenit.move2alf.core.action.resource

import eu.xenit.move2alf.logic.ErrorHandler
import eu.xenit.move2alf.pipeline.actions.context.SendingContext
import eu.xenit.move2alf.common.exceptions.Move2AlfException

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/20/13
 * Time: 2:42 PM
 */
class ResourceErrorHandler(action: ResourceAction[_]) extends ErrorHandler{
  def handleError(actionId: String, message: Any, e: Exception, sendingContext: SendingContext) {
    action.getTaskContext().reply(e)
  }

  def handleError(actionId: String, message: Any, error: String, sendingContext: SendingContext) {
    action.getTaskContext().reply(new Move2AlfException(error))
  }

  def handleInfo(actionId: String, message: Any, info: String, sendingContext: SendingContext) {
    action.getTaskContext().reply(new Move2AlfException(info))
  }

  def handleWarn(actionId: String, message: Any, warning: String, sendingContext: SendingContext) {
    action.getTaskContext().reply(new Move2AlfException(warning))
  }
}
