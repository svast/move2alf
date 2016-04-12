package eu.xenit.move2alf.pipeline.actors

import java.util

import akka.actor._
import akka.testkit.{TestActorRef, TestFSMRef}
import org.junit.{AfterClass, Test}
import eu.xenit.move2alf.pipeline.actions._
import eu.xenit.move2alf.pipeline._

/**
  * Created with IntelliJ IDEA.
  * User: thijs
  * Date: 4/30/13
  * Time: 1:25 PM
  * To change this template use File | Settings | File Templates.
  */
class JobActorTest {


  @Test
  def testStates {

    val startAction = new ActionConfig("startAction", new ActionFactory() {
      def createAction(): Action = new JavaActionImpl[String]
    }, 1)
    val middleAction = new ActionConfig("middleAction", new ActionFactory {
      def createAction(): Action = new DummyEndAction
    }, 1)
    val endAction = new ActionConfig("endAction", new ActionFactory {
      def createAction(): Action = new DummyEndAction
    }, 1)
    startAction.addReceiver("default", middleAction)
    middleAction.addReceiver("default", endAction)
    implicit val system = ActorSystem("testSetStatesActorSystem")
    val actorRef = TestFSMRef(new JobActor("TestId", new JobConfig(startAction, false), new JobInfo))

    assert(actorRef.stateName == NotRunning)
    assert(actorRef.stateData == Uninitialized)

    actorRef ! StartJob(new util.HashMap[String, Object]())
    assert(actorRef.stateName == Running)
    assert(actorRef.stateData.asInstanceOf[CycleData].counter == 1)

    actorRef ! EOC
    assert(actorRef.stateName == NotRunning)
    assert(actorRef.stateData == Uninitialized)

    system.shutdown()
  }

  @Test
  def loopTest = {
    val startAction = new ActionConfig("startAction", new ActionFactory() {
      def createAction(): Action = new JavaActionImpl[String]
    }, 1)
    val middleAction = new ActionConfig("middleAction", new ActionFactory {
      def createAction(): Action = new DummyEndAction
    }, 1)
    val endAction = new ActionConfig("endAction", new ActionFactory {
      def createAction(): Action = new DummyEndAction
    }, 1)

    startAction.addReceiver("default", middleAction)
    middleAction.addReceiver("default", endAction)
    endAction.addReceiver("loop", startAction)
    implicit val system = ActorSystem("LoopTestActorSystem")
    val actorRef = TestFSMRef(new JobActor("TestId", new JobConfig(startAction, true), new JobInfo))

    assert(actorRef.stateName == NotRunning)
    assert(actorRef.stateData == Uninitialized)

    actorRef ! StartJob(new util.HashMap[String, Object]())
    assert(actorRef.stateName == Running)
    assert(actorRef.stateData.asInstanceOf[CycleData].counter == 1)
    system.shutdown()
  }

  @Test
  def testInitialJobConfig {

    var ranInternalTest = false;


    val startAction = new ActionConfig("startAction", new ActionFactory() {
      def createAction(): Action = new JavaActionImpl[String]
    }, 1)
    val middleAction = new ActionConfig("middleAction", new ActionFactory {
      def createAction(): Action = new JavaActionImpl[String] {
        override def execute(message: String): Unit = {
          System.out.println("Reading context!")
          ranInternalTest = true
          assert( stateContext.getStateValue("TestVal1") == "value1")
          assert( stateContext.getStateValue("TestVal2") == "value2")
        }
      }
    }, 1)
    val endAction = new ActionConfig("endAction", new ActionFactory {
      def createAction(): Action = new DummyEndAction
    }, 1)
    startAction.addReceiver("default", middleAction)
    middleAction.addReceiver("default", endAction)
    implicit val system = ActorSystem("testSetStatesActorSystem")
    val info: JobInfo = new JobInfo
    val actorRef = TestFSMRef(new JobActor("TestId", new JobConfig(startAction, false), info))

    assert(actorRef.stateName == NotRunning)
    assert(actorRef.stateData == Uninitialized)

    val initialContextConfig: util.HashMap[String, Object] = new util.HashMap[String, Object]()
    initialContextConfig.put("TestVal1","value1")
    initialContextConfig.put("TestVal2","value2")

    actorRef ! StartJob(initialContextConfig)
    assert(actorRef.stateName == Running)
    assert(actorRef.stateData.asInstanceOf[CycleData].counter == 1)

    Thread.sleep(500)

    val ref = info.getActorRef("middleAction")
    ref.tell(M2AMessage("aaa"))

    Thread.sleep(500)

    system.shutdown()

    assert(ranInternalTest)
  }
}
