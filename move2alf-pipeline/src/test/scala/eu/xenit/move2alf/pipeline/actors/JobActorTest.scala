package eu.xenit.move2alf.pipeline.actors

import akka.actor._
import akka.testkit.{TestFSMRef, TestKit}
import org.junit.Test
import eu.xenit.move2alf.pipeline.actions.{DummyEndAction, JavaActionImpl, DummyStartAction, ActionConfig}
import eu.xenit.move2alf.pipeline.{JobInfo, EOC, Start, AbstractMessage}

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/30/13
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 */
class JobActorTest{


  @Test
  def testStates {

    val startAction = new ActionConfig("startAction", classOf[DummyStartAction], 1)
    val middleAction = new ActionConfig("middleAction", classOf[JavaActionImpl[AbstractMessage]], 1)
    val endAction = new ActionConfig("endAction", classOf[DummyEndAction], 1)
    startAction.addReceiver(middleAction)
    middleAction.addReceiver(endAction)
    implicit val system = ActorSystem("TestSystem")
    val actorRef = TestFSMRef(new JobActor(startAction, new JobInfo))

    assert(actorRef.stateName == NotRunning)
    assert(actorRef.stateData == Uninitialized)

    actorRef ! Start
    assert(actorRef.stateName == Running)
    assert(actorRef.stateData.asInstanceOf[CycleData].counter == 1)

    actorRef ! EOC
    assert(actorRef.stateName == NotRunning)
    assert(actorRef.stateData == Uninitialized)
  }


}
