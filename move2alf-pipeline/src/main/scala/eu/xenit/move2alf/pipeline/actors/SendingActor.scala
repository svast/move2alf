package eu.xenit.move2alf.pipeline.actors

import eu.xenit.move2alf.pipeline.{M2AMessage, AbstractMessage}
import eu.xenit.move2alf.pipeline.actions.SendingAction

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/8/13
 * Time: 11:39 AM
 * To change this template use File | Settings | File Templates.
 */
trait SendingActor[T <: AbstractMessage] extends AbstractM2AActor{

  def sendMessage(message: T) = {
    receiver ! M2AMessage(message)
  }

}
