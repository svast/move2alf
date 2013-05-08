package eu.xenit.move2alf.pipeline.actions.context

import akka.actor.{ActorContext, ActorRef}
import eu.xenit.move2alf.pipeline.state.JobContext
import akka.routing.Broadcast
import eu.xenit.move2alf.pipeline.{AbstractMessage, M2AMessage, EOC}
import eu.xenit.move2alf.common.LogHelper
import eu.xenit.move2alf.pipeline.actions.EOCAware


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/5/13
 * Time: 2:54 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractActionContext(protected val receivers: Map[String, ActorRef], protected val nmbSenders: Int)(implicit protected val jobContext: JobContext, implicit protected val context: ActorContext) extends LogHelper {

  val action: Any

  def receive: PartialFunction[Any, Unit] = {
    case EOC | Broadcast(EOC) => eocMessage()
  }

  protected var nmbOfEOC:Int = 0

  var blocked = false
  var prevented = false

  def blockEOC(){
    blocked = true
  }

  def unblockEOC(){
    blocked = false
    if(prevented) {
      broadCastEOC()
      prevented = false
    }
  }


  protected def eocMessage(){
    logger.debug("Received EOC message")
    nmbOfEOC += 1
    if (nmbOfEOC == nmbSenders) {
      nmbOfEOC = 0
      broadCastEOC()
    }
  }

  protected def broadCastEOC(){
    action match {
      case a: EOCAware => a.beforeSendEOC()
      case _ =>
    }
    if(!blocked){
      logger.debug("Sending EOC message")
      receivers foreach { case (_,receiver) => receiver ! Broadcast(EOC) }
    } else {
      prevented = true
    }
  }

  /**
   * Save a variable that can be retrieved later in this cycle by an Action in the current Job.
   * @param key The key that will be used to retrieve the variable later.
   * @param value  The variable to save.
   */
  final def setStateValue(key:String, value:Any) {
    jobContext.setStateValue(key, value)
  }

  /**
   * Retrieve a variable.
   * @param key Key under which the variable was saved.
   * @return  The state value.
   */
  final def getStateValue(key: String): Any = {
    jobContext.getStateValue(key)
  }


  final def sendMessage(message: AbstractMessage, receiver: String = "default"){
    receivers.get(receiver).get ! M2AMessage(message)
  }

}