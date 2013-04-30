package eu.xenit.move2alf.pipeline.actions.context

import eu.xenit.move2alf.pipeline.{Start, AbstractMessage}


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

  protected def execute()
}
