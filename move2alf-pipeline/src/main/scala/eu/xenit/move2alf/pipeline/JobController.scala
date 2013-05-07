package eu.xenit.move2alf.pipeline

import eu.xenit.move2alf.pipeline.actions.ActionConfig
import eu.xenit.move2alf.pipeline.actors.{Running, JobActor}
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
  private val jobInfos = TMap.empty[String, JobInfo]

  def createJob(id: String, config: ActionConfig){
    val jobInfo = new JobInfo
    val actorRef = system.actorOf(Props(new JobActor(config, jobInfo)), name = id)
    jobs.single += id -> actorRef
    jobInfos.single +=  id -> jobInfo
  }

  def isRunning(jobId: String): Boolean = {
    jobInfos.single.get(jobId).get.state == Running
  }

  def deleteJob(id: String){
    system.stop(jobs.single.get(id).get)
  }

  def startJob(id: String){
    jobs.single.get(id).get ! Start
  }

}
