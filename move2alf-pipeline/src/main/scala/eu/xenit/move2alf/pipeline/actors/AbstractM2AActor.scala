package eu.xenit.move2alf.pipeline.actors

import akka.actor.ActorRef

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/5/13
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
class AbstractM2AActor(receiver: ActorRef) extends PipelineActor() with StateActor{

}
