package eu.xenit.move2alf.pipeline

import akka.actor.ActorRef

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/5/13
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */
case object Start
case object Stop
case object EOC

case class M2AMessage(message: AnyRef)
case class TaskMessage(key: String, message: AnyRef, sendResult: ActorRef)
case class ReplyMessage(key: String, message: AnyRef)

class StringMessage(val string: String)
class StringMessage2(val string: String)
