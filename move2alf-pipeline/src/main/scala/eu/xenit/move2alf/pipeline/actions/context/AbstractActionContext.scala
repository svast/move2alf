package eu.xenit.move2alf.pipeline.actions.context

import akka.actor.{ActorContext, ActorRef}
import eu.xenit.move2alf.pipeline.state.JobContext
import akka.routing.Broadcast
import eu.xenit.move2alf.pipeline._
import eu.xenit.move2alf.common.LogHelper
import eu.xenit.move2alf.pipeline.actions.{AcceptsReply, StartAware, ReceivingAction, EOCAware}
import eu.xenit.move2alf.pipeline.M2AMessage
import eu.xenit.move2alf.pipeline.ReplyMessage
import scala.Some
import akka.routing.Broadcast


/**
 * User: thijs
 * Date: 3/5/13
 * Time: 2:54 PM
 */
abstract class AbstractActionContext(val id: String, protected val receivers: Set[String], getActorRef: String => ActorRef)(implicit protected val jobContext: JobContext, implicit val context: ActorContext) extends LogHelper {

  val action: Any

  private var taskKey: Option[String] = None
  private var replyTo: Option[ActorRef] = None

  def receive(message: AnyRef, key: Option[String] = None, replyTo: Option[ActorRef] = None) {
    this.taskKey = key
    this.replyTo = replyTo
    execute(message)
  }

  def receiveReply(key: String, message: AnyRef) {
    logger.trace("Reply arrived at action: "+context.self)

    action match {
      case a: AcceptsReply => a.acceptReply(key, message)
    }
  }

  protected def execute(message: AnyRef){
    logger.trace("Message arrived at action: "+context.self)

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

  def broadCast(message: AnyRef){
    receivers foreach { receiver => {
      logger.debug(context.self+" broadcasting "+message+" to "+getActorRef(receiver))
      getActorRef(receiver).tell(Broadcast(message), context.self)
    }}
  }

  def flush(){
    action match {
      case a: EOCAware => a.beforeSendEOC()
      case _ =>
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


  //only used to track if a message has been sent
  var messageSent = false

  final def sendMessage(message: AnyRef, receiver: String = "default"){
    if (receivers.contains(receiver)){
      taskKey match {
        case None => getActorRef(receiver).tell(M2AMessage(message), context.self)
        case Some(key) => getActorRef(receiver).tell(TaskMessage(key, message, replyTo.get), context.self)
      }
      messageSent = true
    } else {
      logger.error("Actor: "+context.self+" has no receiver called "+receiver);
    }
  }

  final def hasReceiver(receiver: String): Boolean = {
    receivers.contains(receiver)

  }

  final def getJobId: String = {
    jobContext.jobId
  }

  final def reply(message: AnyRef) = {
    replyTo.get ! ReplyMessage(taskKey.get, message)
  }

}