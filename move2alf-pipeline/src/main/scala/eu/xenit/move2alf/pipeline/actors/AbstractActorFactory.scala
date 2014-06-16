package eu.xenit.move2alf.pipeline.actors

import akka.actor.ActorRef

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/10/13
 * Time: 2:22 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractActorFactory {

  def createActor: ActorRef

}
