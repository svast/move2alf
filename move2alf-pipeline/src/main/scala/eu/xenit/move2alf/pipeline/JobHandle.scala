package eu.xenit.move2alf.pipeline

import eu.xenit.move2alf.pipeline.actions.{JobConfig, ActionConfig}
import eu.xenit.move2alf.pipeline.actors.{Running, JobActor}
import akka.actor._
import eu.xenit.move2alf.common.LogHelper
import java.net.URLEncoder
import scala.collection.mutable

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/6/13
 * Time: 3:37 PM
 * To change this template use File | Settings | File Templates.
 */
class JobHandle(val actorSystem: ActorSystem, val id: String, val config: JobConfig) extends LogHelper{

  private val jobInfo = new JobInfo
  private val actor: ActorRef = actorSystem.actorOf(Props(new JobActor(id, config, jobInfo)), name = URLEncoder.encode(id, "UTF-8"))

  def isRunning(): Boolean = {
    jobInfo.state == Running
  }

  def registerOnStopAction(action: Runnable){
    jobInfo.registerOnStopAction(action)
  }

  def destroy(){
    actorSystem.stop(actor)
    while(!actor.isTerminated){
      Thread.sleep(10)
    }
  }

  def startJob(initJobContext : java.util.Map[String,Object]){
    logger.debug("Starting job: "+id)
    actor ! StartJob(initJobContext)
  }

  def sendTask(key: String, task: Object, replyTo: ActorRef){
    actor ! TaskMessage(key, task, replyTo)
  }

}
