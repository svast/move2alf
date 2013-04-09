package eu.xenit.move2alf.pipeline.actions

import eu.xenit.move2alf.pipeline.AbstractMessage
import eu.xenit.move2alf.pipeline.actors.{AbstractM2AActor, StartActor, SendingActor}


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/6/13
 * Time: 7:56 PM
 * To change this template use File | Settings | File Templates.
 */
trait StartAction[T <: AbstractMessage] extends SendingAction[T] {

  final def execute(act: StartActor[T]) {
    actor = act
    executeImpl()
  }

  def executeImpl()
}
