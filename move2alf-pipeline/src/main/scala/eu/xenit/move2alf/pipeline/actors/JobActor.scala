package eu.xenit.move2alf.pipeline.actors

import akka.actor.{ActorRef, Actor, FSM}
import scala.collection.immutable.HashMap
import eu.xenit.move2alf.pipeline.{EOC, Start}
import akka.routing.Broadcast
import eu.xenit.move2alf.pipeline.actions.ActionConfig
import scala.collection.mutable
import scala.collection.JavaConversions._
import eu.xenit.move2alf.pipeline.state.JobContext


sealed trait JobState
case object Running extends JobState
case object NotRunning extends JobState

sealed trait Data
case object Uninitialized extends Data
case class CycleData(data: HashMap[String, Any], counter: Int) extends Data



/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/29/13
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
class JobActor(private val config: ActionConfig) extends Actor with FSM[JobState, Data]{
  implicit val jobContext = new JobContext

  val (actorRefs, nmbOfSenders) = new PipeLineFactory(self).generateActors(config)

  val firstActor:ActorRef = actorRefs.get(config.getId).get

  startWith(NotRunning, Uninitialized)

  when(NotRunning) {
    case Event(Start, Uninitialized) => {
      firstActor ! Start
      goto(Running) using CycleData(data = new HashMap[String, Any], counter = nmbOfSenders)
    }
  }

  when(Running) {
    case Event(EOC | Broadcast(EOC), data: CycleData) => {
      data.counter match {
        case 1 => goto(NotRunning) using Uninitialized
        case _ => stay using data.copy(counter = data.counter - 1)
      }
    }
  }

  initialize

}
