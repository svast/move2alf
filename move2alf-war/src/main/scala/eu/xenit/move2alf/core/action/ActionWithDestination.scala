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
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/19/13
 * Time: 3:33 PM
 */
abstract class ActionWithDestination[T, U] extends Move2AlfReceivingAction[T] with EOCBlockingAction with AcceptsReply[U] with LogHelper {

  @Autowired private var destinationService: DestinationService = null

  private var destination: Int = 0

  def setDestination(dest: String) {
    this.destination = Integer.parseInt(dest)
  }

  def getDestination: Int = {
    return destination
  }

  private val replyHandlers: mutable.Map[String, (U => Unit)] = new mutable.HashMap
  private val messages: mutable.Map[String, AnyRef] = new mutable.HashMap

  protected def sendTaskToDestination(message: AnyRef, replyHandler: (U => Unit)) {
    if (replyHandlers.size == 0) {
      eocBlockingContext.blockEOC
    }
    val key: String = Integer.toString(message.hashCode)
    destinationService.sendTaskToDestination(destination, key, message, stateContext.getActorRef)
    replyHandlers.put(key, replyHandler)
    messages.put(key, message)
  }

  def acceptReply(key: String, message: U) {
    if (message.isInstanceOf[Exception]) {
      handleError(messages.get(key).get, message.asInstanceOf[Exception])
    }
    else {
      replyHandlers.get(key).get.apply(message)
    }
    replyHandlers.remove(key)
    messages.remove(key)
    if (replyHandlers.size == 0) {
      eocBlockingContext.unblockEOC
    }
  }

  private var eocBlockingContext: EOCBlockingContext = null

  def setEOCBlockingContext(eocBlockingContext: EOCBlockingContext) {
    this.eocBlockingContext = eocBlockingContext
  }

}

object ActionWithDestination {
  final val PARAM_DESTINATION: String = "destination"
}
