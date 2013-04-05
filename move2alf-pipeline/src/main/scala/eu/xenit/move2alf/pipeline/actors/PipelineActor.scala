package eu.xenit.move2alf.pipeline.actors

import eu.xenit.move2alf.pipeline.EOC
import akka.actor._
import akka.routing.Broadcast
import eu.xenit.move2alf.common.LogHelper
import eu.xenit.move2alf.pipeline.state.JobContext

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/6/13
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
class PipelineActor(val receiver: ActorRef, val nmbSenders: Int = 1)(implicit val jobContext: JobContext) extends Actor with LogHelper{

  private var nmbOfEOC:Int = 0;

  def receive = {
    case Broadcast(EOC) | EOC => eocMessage()
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