package eu.xenit.move2alf.pipeline.actions.context

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/12/13
 * Time: 3:43 PM
 */
class TaskContextImpl(private val ct: AbstractActionContext) extends TaskContext{

  def reply(message: AnyRef) {
    ct.reply(message)
  }
}
