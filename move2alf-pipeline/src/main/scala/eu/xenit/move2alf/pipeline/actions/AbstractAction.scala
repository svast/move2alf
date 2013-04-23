package eu.xenit.move2alf.pipeline.actions

import akka.actor.ActorRef
import eu.xenit.move2alf.pipeline.state.JobContext
import akka.routing.Broadcast
import eu.xenit.move2alf.pipeline.{M2AMessage, EOC}
import eu.xenit.move2alf.common.LogHelper


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/5/13
 * Time: 2:54 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractAction(protected val receiver: ActorRef, private val nmbSenders: Int)(implicit protected val jobContext: JobContext) extends LogHelper {

  def receive: PartialFunction[Any, Unit] = {
    case EOC => eocMessage()
  }

  private var nmbOfEOC:Int = 0

  final protected def setStateValue(key:String, value:Any) {
    jobContext.setStateValue(key, value)
  }

  final protected def getStateValue(key: String): Any = {
    jobContext.getStateValue(key)
  }

  protected def eocMessage(){
    logger.debug("Recieved EOC message")
    nmbOfEOC += 1
    if (nmbOfEOC == nmbSenders) {
      nmbOfEOC = 0
      logger.debug("Sending EOC message")
      receiver ! Broadcast(EOC)
    }
  }
}
