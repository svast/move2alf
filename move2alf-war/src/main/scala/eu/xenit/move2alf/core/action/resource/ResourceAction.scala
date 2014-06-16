package eu.xenit.move2alf.core.action.resource

import eu.xenit.move2alf.pipeline.actions.{AbstractBasicAction, HasTaskContext}
import eu.xenit.move2alf.pipeline.actions.context.TaskContext
import eu.xenit.move2alf.core.action.Move2AlfReceivingAction

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/20/13
 * Time: 2:27 PM
 */
abstract class ResourceAction[T] extends Move2AlfReceivingAction[T] with HasTaskContext{

  setErrorHandler(new ResourceErrorHandler(this))

  protected var taskContext: TaskContext = _
  def setTaskContext(taskContext: TaskContext) {
    this.taskContext = taskContext
  }

  def getTaskContext(): TaskContext = {
    this.taskContext
  }

  def reply(message: AnyRef){
    taskContext.reply(message)
  }
}
