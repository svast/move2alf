package eu.xenit.move2alf.pipeline.actors

import eu.xenit.move2alf.pipeline.{M2AMessage, AbstractMessage}
import eu.xenit.move2alf.pipeline.actions.ReceivingAction

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/8/13
 * Time: 11:36 AM
 * To change this template use File | Settings | File Templates.
 */
trait ReceivingActor[T <: AbstractMessage] extends AbstractM2AActor{

  protected[actors] override def action: ReceivingAction[T] = super.action.asInstanceOf[ReceivingAction[T]]

  override def receive = {
    case M2AMessage(message) => execute(message.asInstanceOf[T])
    case s => super.receive(s)
  }

  protected def execute(message: T){
    action.execute(message, this)
  }

}
