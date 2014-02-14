package eu.xenit.move2alf.pipeline.actors

import org.junit.{Test}
import akka.testkit.{TestActorRef, TestProbe, TestFSMRef}
import eu.xenit.move2alf.pipeline.actions.context.{AbstractActionContext, AbstractActionContextFactory}
import eu.xenit.move2alf.pipeline.actions.{ActionFactory, Action}
import akka.actor._
import org.mockito.Mockito._
import eu.xenit.move2alf.pipeline.{Start, EOC}
import eu.xenit.move2alf.pipeline.state.JobContext
import akka.routing.Broadcast


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 2/5/14
 * Time: 3:19 PM
 * To change this template use File | Settings | File Templates.
 */
class M2AActorTest {

  implicit val system = ActorSystem("test")
  val actionFactory = new ActionFactory {
    def createAction(): Action = mock(classOf[Action])
  }

  implicit val jobContext = mock(classOf[JobContext])

  val testProbe = TestProbe()

  def getReceiver(s: String): ActorRef = {
    if(s=="receiver1"){
      testProbe.ref
    } else {
      assert(false)
      return null
    }
  }

  val actionContextFactory = new AbstractActionContextFactory("test", actionFactory) {
    protected def constructActionContext(basicAction: Action)(implicit context: ActorContext): AbstractActionContext = new AbstractActionContext("TestActor", Set("receiver1"), getReceiver) {
      val action: Any = actionFactory.createAction()
    }
  }

  @Test
  def testAliveTransition {
    val testFSM = TestFSMRef(new M2AActor(actionContextFactory, 3, 0, actionId => 0))
    assert(testFSM.stateName==Death)
    testFSM ! Start
    assert(testFSM.stateName==Alive)
    testFSM ! EOC
    assert(testFSM.stateName==Alive)
    testFSM ! EOC
    assert(testFSM.stateName==Alive)
    testFSM ! EOC
    assert(testFSM.stateName==Death)
  }

  @Test
  def testNegotiatingTransition {
    val testFSM = TestFSMRef(new M2AActor(actionContextFactory, 2, 1, actionId => 0))
    assert(testFSM.stateName==Death)
    testFSM ! Start
    assert(testFSM.stateName==Alive)
    testFSM ! EOC
    assert(testFSM.stateName==Alive)
    testFSM ! EOC
    assert(testFSM.stateName==Negotiating)
  }

  @Test
  def testRenegotiateReply {
    val testFSM = TestFSMRef(new M2AActor(actionContextFactory, 2, 1, actionId => 0))
    val testRef = TestActorRef[TestActor]
    assert(testFSM.stateName==Death)
    testFSM ! Start
    testProbe.expectMsg(Broadcast(Start))
    assert(testFSM.stateName==Alive)
    val negotiate = Negotiate(Seq(testRef, testFSM))
    testFSM ! negotiate
    testProbe.expectNoMsg()
    testFSM ! negotiate
    testProbe.expectNoMsg()
    testFSM ! negotiate
    testProbe.expectMsgPF()( {
     case Broadcast(Negotiate(array)) => {
       assert(array.last == testRef)
     }
    })
  }

  @Test
  def testAliveForwardNegotiate {
    val testFSM = TestFSMRef(new M2AActor(actionContextFactory, 2, 2, actionId => 0))
    val testRef = TestActorRef[TestActor]
    assert(testFSM.stateName==Death)
    testFSM ! Start
    testProbe.expectMsg(Broadcast(Start))
    val negotiate = Negotiate(Seq(testFSM, testRef))
    testFSM ! negotiate
    testProbe.expectNoMsg()
    testFSM ! negotiate
    testProbe.expectMsgPF()( {
      case Broadcast(Negotiate(seq)) => {
        assert(seq.equals(negotiate.actors))
      }
    })
  }

  @Test
  def testRenegotiateSend {
    val testFSM = TestFSMRef(new M2AActor(actionContextFactory, 3, 2, actionId => 0))
    val testRef = TestActorRef[TestActor]
    assert(testFSM.stateName==Death)
    testFSM ! Start
    testProbe.expectMsg(Broadcast(Start))
    val negotiate = Negotiate(Seq(testRef))
    testFSM ! negotiate
    testProbe.expectNoMsg()
    testFSM ! negotiate
    testProbe.expectNoMsg()
    testFSM ! negotiate
    testProbe.expectMsgPF()( {
      case Broadcast(Negotiate(seq)) => {
        assert(seq.toSeq.equals(Seq(testRef, testFSM)))
      }
    })
  }

  @Test
  def testNegotiateToFlushToDeath {
    val testFSM= TestFSMRef(new M2AActor(actionContextFactory, 2,3, actionId => 0))
    assert(testFSM.stateName==Death)
    testFSM ! Start
    testFSM ! EOC
    testFSM ! EOC
    (1 to 3).foreach(ignored => testFSM ! Negotiate(Seq(testFSM)))
    assert(testFSM.stateName == Flushing)
    (1 to 3).foreach(ignored => testFSM ! Flush(Seq(testFSM)))
    assert(testFSM.stateName == NearDeath)
    (1 to 3).foreach(ignored => testFSM ! ReadyToDie)
    assert(testFSM.stateName == Death)
  }

}
