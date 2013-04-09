package eu.xenit.move2alf.pipeline.actions

import eu.xenit.move2alf.pipeline.AbstractMessage
import eu.xenit.move2alf.pipeline.actors.{ReceivingActor, AbstractM2AActor}


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/6/13
 * Time: 8:06 PM
 * To change this template use File | Settings | File Templates.
 */
trait ReceivingAction[T <: AbstractMessage] extends AbstractAction {

  final def execute(message: T, _actor: ReceivingActor[T]){
    this.actor = _actor
    executeImpl(message)
  }

  def executeImpl(message: T)
}
