package eu.xenit.move2alf.pipeline.actors

import akka.actor.{ActorRef, Actor, FSM}
import scala.collection.immutable.HashMap
import eu.xenit.move2alf.pipeline.{EOC, Start}
import akka.routing.Broadcast
import eu.xenit.move2alf.pipeline.actions.ActionConfig
import scala.collection.mutable
import scala.collection.JavaConversions._


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

  private val actorRefs = mutable.Map[String, ActorRef] = new mutable.HashMap[String, ActorRef]()


  private def generateActors(config: ActionConfig){
    val countedActionConfigs = new mutable.HashMap[ActionConfig, Int]()

    def countSenders(config: ActionConfig) {
      config.getReceivers() foreach {
        ac => {
          val nmbSenders = countedActionConfigs.getOrElseUpdate(ac, 0)
          countedActionConfigs.update(ac, nmbSenders + 1)
          countSenders(ac)
        }
      }
    }
    countSenders(config)

    def makeActors(config: ActionConfig) {
      config.getReceivers() foreach {
        ac => {
          makeActors(ac)
          //TODO
        }
      }
    }


  }

  val firstActor:ActorRef = _
  val nmbofSenders = 5

  startWith(NotRunning, Uninitialized)

  when(NotRunning) {
    case Event(Start, Uninitialized) => {
      firstActor ! Start
      goto(Running) using CycleData(data = new HashMap[String, Any], counter = nmbofSenders)
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
