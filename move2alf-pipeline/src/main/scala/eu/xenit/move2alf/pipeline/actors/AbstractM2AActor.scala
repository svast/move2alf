package eu.xenit.move2alf.pipeline.actors

import akka.actor.ActorRef
import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.actions.AbstractAction

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/5/13
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
class AbstractM2AActor(private var _action: AbstractAction, receiver: ActorRef, nmbOfSenders: Int)(implicit jobContext: JobContext) extends PipelineActor(receiver, nmbOfSenders) with StateActor{

  protected[actors] def action: AbstractAction = _action

}