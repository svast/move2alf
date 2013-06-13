package eu.xenit.move2alf.pipeline.actors

import akka.actor._
import akka.testkit.TestFSMRef
import org.junit.Test
import eu.xenit.move2alf.pipeline.actions.{DummyEndAction, JavaActionImpl, DummyStartAction, ActionConfig}
import eu.xenit.move2alf.pipeline.{JobInfo, EOC, Start}

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

//    val startAction = new ActionConfig("startAction", classOf[DummyStartAction], 1)
//    val middleAction = new ActionConfig("middleAction", classOf[JavaActionImpl[_]], 1)
//    val endAction = new ActionConfig("endAction", classOf[DummyEndAction], 1)
//    startAction.addReceiver("default",middleAction)
//    middleAction.addReceiver("default",endAction)
//    implicit val system = ActorSystem("TestSystem3")
//    val actorRef = TestFSMRef(new JobActor("TestId",startAction, new JobInfo))
//
//    assert(actorRef.stateName == NotRunning)
//    assert(actorRef.stateData == Uninitialized)
//
//    actorRef ! Start
//    assert(actorRef.stateName == Running)
//    assert(actorRef.stateData.asInstanceOf[CycleData].counter == 1)
//
//    actorRef ! EOC
//    assert(actorRef.stateName == NotRunning)
//    assert(actorRef.stateData == Uninitialized)
//
//    system.shutdown()
  }


}
