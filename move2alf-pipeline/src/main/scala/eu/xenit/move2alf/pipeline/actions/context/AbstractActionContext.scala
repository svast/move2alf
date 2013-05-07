package eu.xenit.move2alf.pipeline.actions.context

import akka.actor.{ActorContext, ActorRef}
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
abstract class AbstractActionContext(protected val receivers: Map[String, ActorRef], private val nmbSenders: Int)(implicit protected val jobContext: JobContext, implicit protected val context: ActorContext) extends LogHelper {

  def receive: PartialFunction[Any, Unit] = {
    case EOC | Broadcast(EOC) => eocMessage()
  }

  private var nmbOfEOC:Int = 0

  protected def eocMessage(){
    logger.debug("Received EOC message")
    nmbOfEOC += 1
    if (nmbOfEOC == nmbSenders) {
      nmbOfEOC = 0
      broadCastEOC()
    }
  }

  protected def broadCastEOC(){
    logger.debug("Sending EOC message")
    receivers foreach { case (_,receiver) => receiver ! Broadcast(EOC) }
  }

}
