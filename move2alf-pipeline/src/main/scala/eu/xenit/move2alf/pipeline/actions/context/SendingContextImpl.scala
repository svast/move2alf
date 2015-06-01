package eu.xenit.move2alf.pipeline.actions.context


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/25/13
 * Time: 4:23 PM
 * To change this template use File | Settings | File Templates.
 */
class SendingContextImpl(private val ct: AbstractActionContext) extends SendingContext{
  def sendMessage(message: AnyRef) {
    ct.sendMessage(message)
  }

  def sendMessage(message: AnyRef, receiver: String) {
    ct.sendMessage(message, receiver)
  }

  def hasReceiver(receiver: String): Boolean = {
    ct.hasReceiver(receiver)
  }

  def broadcastPublic(message: AnyRef) {
    ct.broadcastPublic(message);
  }
}
