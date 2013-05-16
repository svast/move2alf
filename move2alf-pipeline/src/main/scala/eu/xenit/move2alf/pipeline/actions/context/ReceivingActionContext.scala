package eu.xenit.move2alf.pipeline.actions.context

import eu.xenit.move2alf.pipeline.{M2AMessage, AbstractMessage}
import eu.xenit.move2alf.pipeline.actions.ReceivingAction

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/6/13
 * Time: 8:06 PM
 * To change this template use File | Settings | File Templates.
 */
trait ReceivingActionContext[T <: AbstractMessage] extends AbstractActionContext {

  override def receive = {
    case M2AMessage(message) => execute(message.asInstanceOf[T])
    case s => super.receive(s)
  }

  protected def execute(message: T){
    action match {
      case a: ReceivingAction[T] => a.execute(message)
    }
  }

}
