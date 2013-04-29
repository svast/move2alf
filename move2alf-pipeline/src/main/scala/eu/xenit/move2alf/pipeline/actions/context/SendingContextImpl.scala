package eu.xenit.move2alf.pipeline.actions.context

import eu.xenit.move2alf.pipeline.AbstractMessage

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/25/13
 * Time: 4:23 PM
 * To change this template use File | Settings | File Templates.
 */
class SendingContextImpl(private val ct: SendingActionContext) extends SendingContext{
  def sendMessage(message: AbstractMessage) {
    ct.sendMessage(message)
  }

  def sendMessage(message: AbstractMessage, receiver: String) {
    ct.sendMessage(message, receiver)
  }
}
