package eu.xenit.move2alf.pipeline.actors

import akka.actor._
import akka.testkit.TestFSMRef
import org.junit.Test
import eu.xenit.move2alf.pipeline.actions._
import eu.xenit.move2alf.pipeline.{JobInfo, EOC, Start}
import eu.xenit.move2alf.pipeline.actors.CycleData

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

    val startAction = new ActionConfig("startAction", new ActionFactory(){
      def createAction(): Action = new JavaActionImpl[String]
    }, 1)
    val middleAction = new ActionConfig("middleAction", new ActionFactory {
      def createAction(): Action = new DummyEndAction
    }, 1)
    val endAction = new ActionConfig("endAction", new ActionFactory {
      def createAction(): Action = new DummyEndAction
    }, 1)
    startAction.addReceiver("default",middleAction)
    middleAction.addReceiver("default",endAction)
    implicit val system = ActorSystem("TestSystem3")
    val actorRef = TestFSMRef(new JobActor("TestId", new JobConfig(startAction, false), new JobInfo))

    assert(actorRef.stateName == NotRunning)
    assert(actorRef.stateData == Uninitialized)

    actorRef ! Start
    assert(actorRef.stateName == Running)
    assert(actorRef.stateData.asInstanceOf[CycleData].counter == 1)

    actorRef ! EOC
    assert(actorRef.stateName == NotRunning)
    assert(actorRef.stateData == Uninitialized)

    system.shutdown()
  }

  @Test
  def loopTest = {
    val startAction = new ActionConfig("startAction", new ActionFactory(){
      def createAction(): Action = new JavaActionImpl[String]
    }, 1)
    val middleAction = new ActionConfig("middleAction", new ActionFactory {
      def createAction(): Action = new DummyEndAction
    }, 1)
    val endAction = new ActionConfig("endAction", new ActionFactory {
      def createAction(): Action = new DummyEndAction
    }, 1)

    startAction.addReceiver("default",middleAction)
    middleAction.addReceiver("default",endAction)
    endAction.addReceiver("loop", startAction)
    implicit val system = ActorSystem("TestSystem4")
    val actorRef = TestFSMRef(new JobActor("TestId", new JobConfig(startAction, true), new JobInfo))

    assert(actorRef.stateName == NotRunning)
    assert(actorRef.stateData == Uninitialized)

    actorRef ! Start
    assert(actorRef.stateName == Running)
    assert(actorRef.stateData.asInstanceOf[CycleData].counter == 1)



  }


}
