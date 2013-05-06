package eu.xenit.move2alf.pipeline

import eu.xenit.move2alf.pipeline.actions.ActionConfig
import eu.xenit.move2alf.pipeline.actors.JobActor
import concurrent.stm._
import akka.actor._

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/6/13
 * Time: 3:37 PM
 * To change this template use File | Settings | File Templates.
 */
object JobController {

  private val system = ActorSystem("Move2Alf")
  private val jobs = TMap.empty[String, ActorRef]

  def createJob(id: String, config: ActionConfig){
    val actorRef = system.actorOf(Props(new JobActor(config)))
    jobs.single += id -> actorRef
  }

  def deleteJob(id: String){
    system.stop(jobs.single.get(id).get)
  }

  def startJob(id: String){
    jobs.single.get(id).get ! Start
  }

}
