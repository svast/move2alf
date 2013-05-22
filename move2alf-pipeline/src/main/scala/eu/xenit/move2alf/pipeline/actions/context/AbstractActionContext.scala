package eu.xenit.move2alf.pipeline.actions.context

import akka.actor.{ActorContext, ActorRef}
import eu.xenit.move2alf.pipeline.state.JobContext
import akka.routing.Broadcast
import eu.xenit.move2alf.pipeline.{Start, M2AMessage, EOC}
import eu.xenit.move2alf.common.LogHelper
import eu.xenit.move2alf.pipeline.actions.{StartAware, ReceivingAction, EOCAware}


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/5/13
 * Time: 2:54 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractActionContext(val id: String, protected val receivers: Map[String, ActorRef])(implicit protected val jobContext: JobContext, implicit protected val context: ActorContext) extends LogHelper {

  val action: Any

  def receive(message: AnyRef) {
    execute(message)
  }

  protected def execute(message: AnyRef){
    logger.debug("Message arrived at action: "+context.self)

    action match {
      case a: ReceivingAction[AnyRef@unchecked] => a.execute(message)
    }
  }

  def onStart(){
    action match {
      case a: StartAware => a.onStart()
      case _ =>
    }
  }

  var blocked = false

  def blockEOC(){
    blocked = true
  }

  def unblockEOC(){
    blocked = false
  }

  def sendStartMessage(){
    receivers foreach { case (_,receiver) => receiver ! Broadcast(Start)}
  }

  def broadCastEOC(){
    action match {
      case a: EOCAware => a.beforeSendEOC()
      case _ =>
    }

    logger.debug(context.self+"Sending EOC message")
    receivers foreach { case (_,receiver) => receiver ! Broadcast(EOC) }

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


  final def sendMessage(message: AnyRef, receiver: String = "default"){
    receivers.get(receiver).get ! M2AMessage(message)
  }

  final def hasReceiver(receiver: String): Boolean = {
    receivers.contains(receiver)
  }

  final def getJobId: String = {
    jobContext.jobId
  }

}