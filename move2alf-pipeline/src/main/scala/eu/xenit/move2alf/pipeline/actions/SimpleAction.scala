package eu.xenit.move2alf.pipeline.actions

import eu.xenit.move2alf.pipeline.AbstractMessage
import akka.actor.ActorRef
import eu.xenit.move2alf.pipeline.state.JobContext


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/7/13
 * Time: 9:58 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class SimpleAction[T <: AbstractMessage, V <: AbstractMessage](receiver: ActorRef, nmbSenders: Int)(implicit jobContext: JobContext) extends AbstractAction(receiver, nmbSenders) with ReceivingAction[T] with SendingAction[V]{

}
