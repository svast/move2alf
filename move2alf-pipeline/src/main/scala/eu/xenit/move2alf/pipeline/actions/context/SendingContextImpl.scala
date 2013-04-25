package eu.xenit.move2alf.pipeline.actions.context

import eu.xenit.move2alf.pipeline.AbstractMessage

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/25/13
 * Time: 4:23 PM
 * To change this template use File | Settings | File Templates.
 */
class SendingContextImpl[T <: AbstractMessage](private val ct: SendingActionContext[T]) extends StateContextImpl(ct) with SendingContext[T]{
  def sendMessage(message: T) {
    ct.sendMessage(message)
  }

  def sendMessage(message: T, receiver: String) {
    ct.sendMessage(message, receiver)
  }
}
