package eu.xenit.move2alf.pipeline.actions

import eu.xenit.move2alf.pipeline.{M2AMessage, AbstractMessage}

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/6/13
 * Time: 8:06 PM
 * To change this template use File | Settings | File Templates.
 */
trait ReceivingAction[T <: AbstractMessage] extends AbstractAction {

  override def receive = {
    case M2AMessage(message) => execute(message.asInstanceOf[T])
    case s => super.receive(s)
  }

  protected def execute(message: T)

}
