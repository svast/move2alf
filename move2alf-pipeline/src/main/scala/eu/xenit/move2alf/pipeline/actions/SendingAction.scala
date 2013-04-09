package eu.xenit.move2alf.pipeline.actions

import eu.xenit.move2alf.pipeline.AbstractMessage
import eu.xenit.move2alf.pipeline.actors.{AbstractM2AActor, SendingActor}


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/6/13
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */
trait SendingAction[T <: AbstractMessage] extends AbstractAction {

  final protected def sendMessage(message: T){
    actor.asInstanceOf[SendingActor[T]].sendMessage(message)
  }
}
