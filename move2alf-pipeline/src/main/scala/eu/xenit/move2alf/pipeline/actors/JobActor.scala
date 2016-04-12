package eu.xenit.move2alf.pipeline.actors

import akka.actor.{ActorRef, Actor, FSM}
import eu.xenit.move2alf.pipeline._
import eu.xenit.move2alf.pipeline.actions.JobConfig
import eu.xenit.move2alf.pipeline.state.JobContext
import akka.routing.Broadcast
import eu.xenit.move2alf.common.LogHelper

import scala.collection.JavaConversions


sealed trait JobState
case object Running extends JobState
case object NotRunning extends JobState

sealed trait Data
case object Uninitialized extends Data
case class CycleData(counter: Int) extends Data



/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/29/13
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
class JobActor(val id: String, private val config: JobConfig, private val jobInfo: JobInfo) extends Actor with FSM[JobState, Data] with LogHelper{
  implicit val jobContext = new JobContext(id)

  val (actorRefs, nmbOfSenders) = new PipeLineFactory(self).generateActors(config.getFirstAction)
  jobInfo.setActorRefs(actorRefs)

  val firstActor:ActorRef = actorRefs.get(config.getFirstAction.getId).get

  startWith(NotRunning, Uninitialized)

  when(NotRunning) {
    case Event(Start, Uninitialized) => {
      throw new RuntimeException("Replaced with StartJob, should not be called anymore!")
    }
    case Event(StartJob(initialJobConfig), Uninitialized) => {
      firstActor ! Broadcast(Start)

      JavaConversions.asScalaMap(initialJobConfig)
        .foreach{case(k,v) => jobContext.setStateValue(k,v)};

      if (config.isAutoStop) firstActor ! Broadcast(EOC)
      goto(Running) using CycleData(counter = nmbOfSenders)
    }
  }

  when(Running) {
    case Event(EOC | Broadcast(EOC), data: CycleData) => {
      logger.debug(context.self+" received EOC, current counter = "+(data.counter-1))
      data.counter match {
        case 1 => goto(NotRunning) using Uninitialized
        case _ => stay using data.copy(counter = data.counter - 1)
      }
    }
    case Event(Start | Broadcast(Start), _) => stay
    case Event(Stop, _) => {
      firstActor ! Broadcast(EOC)
      stay
    }
    case Event(TaskMessage(key, message, ref), _) => {
      firstActor ! TaskMessage(key, message, ref)
      stay
    }
    case Event(Negotiate(_) | Broadcast(Negotiate(_)) | Flush(_) | Broadcast(Flush(_)) | ReadyToDie | Broadcast(ReadyToDie) | BackAlive | Broadcast(BackAlive), _) => {
      stay()
    }
  }

  onTransition {
    case _ -> Running => {
      jobInfo.state = Running
    }
    case _ -> NotRunning => {
      jobInfo.state = NotRunning
      jobInfo.onStopActions.foreach(action => {
        action.run()
      }
      )
    }
  }

  initialize

}
