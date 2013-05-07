package eu.xenit.move2alf.pipeline.actions.context

import eu.xenit.move2alf.pipeline.{Start, AbstractMessage}
import eu.xenit.move2alf.pipeline.actions.BeginAction


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/6/13
 * Time: 7:56 PM
 * To change this template use File | Settings | File Templates.
 */
trait StartActionContext extends SendingActionContext {

  override def receive = {
    case Start => execute()
    case s => super.receive(s)
  }

  val action: BeginAction
  protected def execute() {
    action.executeImpl()
    broadCastEOC()
  }
}
