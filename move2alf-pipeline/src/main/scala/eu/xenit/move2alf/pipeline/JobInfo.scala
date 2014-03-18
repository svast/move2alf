package eu.xenit.move2alf.pipeline

import eu.xenit.move2alf.pipeline.actors.{NotRunning, JobState}
import akka.actor.ActorRef
import scala.collection.mutable

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/6/13
 * Time: 3:41 PM
 * To change this template use File | Settings | File Templates.
 */
class JobInfo {

  var state: JobState = NotRunning

  private var actorRefs: Map[String, ActorRef] = _

  def setActorRefs(actorRefs: Map[String, ActorRef]){
    this.actorRefs = actorRefs
  }

  def getActorRef(actionId: String){
    actorRefs.get(actionId).get
  }

  val onStopActions = new mutable.HashSet[Runnable]

  def registerOnStopAction(action: Runnable){
    onStopActions += action
  }

}
