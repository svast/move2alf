package eu.xenit.move2alf.pipeline.actions.context


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/25/13
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 */
trait SendingContext{


  /**
   * Send a message to the default receiver
   * @param message
   */
  def sendMessage(message: AnyRef)

  def sendMessage(message: AnyRef, receiver: String)

  def hasReceiver(receiver: String): Boolean

}
