package eu.xenit.move2alf.core.action

import eu.xenit.move2alf.pipeline.actions.{AcceptsReply, EOCBlockingAction}
import eu.xenit.move2alf.common.LogHelper
import eu.xenit.move2alf.logic.DestinationService
import scala.Predef.String
import scala.Exception
import eu.xenit.move2alf.pipeline.actions.context.EOCBlockingContext
import org.springframework.beans.factory.annotation.Autowired
import scala.collection.mutable

/**
 * Proxy to send message from a normal move2alf job context to a destination job context
 * Use both for alfresco and castor indifferently
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/19/13
 * Time: 3:33 PM
 */
abstract class ActionWithDestination[T, U] extends Move2AlfReceivingAction[T] with EOCBlockingAction with AcceptsReply with LogHelper {

  @Autowired protected var destinationService: DestinationService = null

  def getDestinationService: DestinationService = {
    return destinationService;
  }

  private var destination: Int = -1

  def setDestination(dest: String) {
    this.destination = Integer.parseInt(dest)
  }

  def getDestination: Int = {
    return destination
  }

  private val replyHandlers: mutable.Map[String, (U => Unit)] = new mutable.HashMap
  private val messages: mutable.Map[String, AnyRef] = new mutable.HashMap
  private val originals: mutable.Map[String, T] = new mutable.HashMap
  private var messageCounter = 0

  protected def sendTaskToDestination(original: T, message: AnyRef, replyHandler: (U => Unit)) {
    if (replyHandlers.size == 0) {
      eocBlockingContext.blockEOC
    }
    val key: String = stateContext.getActorRef().toString()+" "+messageCounter;
    messageCounter = messageCounter + 1
    replyHandlers.put(key, replyHandler)
    messages.put(key, message)
    originals.put(key, original)
    logger.debug("Sending message from actor {} to destination {}", stateContext.getActorRef().toString(), destination)
    destinationService.sendTaskToDestination(destination, key, message, stateContext.getActorRef)
  }

  def acceptReply(key: String, message: AnyRef) {
    try {
      message match {
        case message: Exception => handleErrorReply(originals.get(key).get, messages.get(key).get, message)
        case message: U@unchecked => replyHandlers.get(key).getOrElse({ message: AnyRef => {
          logger.error("No reply method for message " + message.toString + " Message key: " + key)
          handleError(message, "Something went wrong while putting message.")
        }
        }).apply(message)
      }
    } catch {
      case e: Exception => {
        logger.error("Error in acceptReply " + e + " for message " + message, e)
        handleError(originals.get(key).get, e)
      }
    }
    replyHandlers.remove(key)
    messages.remove(key)
    originals.remove(key)
    if (replyHandlers.size == 0) {
      eocBlockingContext.unblockEOC
    }
  }

  /**
   * @param original The original incoming message
   * @param message The outgoing message for which the error reply occurred
   * @param reply The Exception that happened in the reply
   */
  protected def handleErrorReply(original: T, message: AnyRef, reply: Exception){
    handleError(original, reply)
  }

  private var eocBlockingContext: EOCBlockingContext = null

  def setEOCBlockingContext(eocBlockingContext: EOCBlockingContext) {
    this.eocBlockingContext = eocBlockingContext
  }

}

object ActionWithDestination {
  final val PARAM_DESTINATION: String = "destination"
}
