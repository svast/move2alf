package eu.xenit.move2alf.pipeline

import eu.xenit.move2alf.pipeline.actions.{JobConfig, ActionConfig}
import eu.xenit.move2alf.pipeline.actors.{Running, JobActor}
import akka.actor._

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/6/13
 * Time: 3:37 PM
 * To change this template use File | Settings | File Templates.
 */
class JobHandle(val actorSystem: ActorSystem, val id: String, val config: JobConfig) {

  private val jobInfo = new JobInfo
  private val actor: ActorRef = actorSystem.actorOf(Props(new JobActor(id, config, jobInfo)), name = id)

  def isRunning(): Boolean = {
    jobInfo.state == Running
  }

  def destroy(){
    actorSystem.stop(actor)
  }

  def startJob(){
    actor ! Start
  }

  def sendTask(key: String, task: Object, replyTo: ActorRef){
    actor ! TaskMessage(key, task, replyTo)
  }

}
