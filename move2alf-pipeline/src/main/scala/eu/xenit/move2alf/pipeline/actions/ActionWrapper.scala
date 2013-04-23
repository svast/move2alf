package eu.xenit.move2alf.pipeline.actions

import eu.xenit.move2alf.pipeline.AbstractMessage
import collection.JavaConversions._
import akka.actor.ActorRef
import eu.xenit.move2alf.pipeline.state.JobContext

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/9/13
 * Time: 9:56 AM
 * To change this template use File | Settings | File Templates.
 */
class ActionWrapper[T <: AbstractMessage, V <: AbstractMessage](private val action: BasicAction[T,V], receiver: ActorRef, nmbSenders: Int)(implicit jobContext: JobContext) extends SimpleAction[T,V](receiver, nmbSenders){

  def execute(message: T) {
    action.executeImpl(message)
  }
}
