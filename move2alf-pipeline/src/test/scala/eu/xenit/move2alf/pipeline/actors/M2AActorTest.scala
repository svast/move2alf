package eu.xenit.move2alf.pipeline.actors

import org.junit.{After, Before, Test}
import akka.testkit.{TestActorRef, TestProbe, TestFSMRef}
import eu.xenit.move2alf.pipeline.actions.context.{AbstractActionContext, AbstractActionContextFactory}
import eu.xenit.move2alf.pipeline.actions.{JavaActionImpl, ActionFactory, Action}
import akka.actor._
import org.mockito.Mockito._
import eu.xenit.move2alf.pipeline.{M2AMessage, Start, EOC}
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

  implicit var system: ActorSystem = _
  var actionFactory: ActionFactory = _
  var testProbe: TestProbe = _
  var actionContextFactory: AbstractActionContextFactory = _

  @Before
  def setupActorSystem{
    system = ActorSystem("testM2AActorSystem")
    testProbe = TestProbe()
    actionFactory = new ActionFactory {
      def createAction(): Action = new JavaActionImpl[String]
    }
    actionContextFactory = new AbstractActionContextFactory("test", actionFactory) {
      protected def constructActionContext(basicAction: Action)(implicit context: ActorContext): AbstractActionContext = new AbstractActionContext(testFSMId, Set("receiver1"), getReceiver) {
        val action: Any = actionFactory.createAction()
      }
    }
  }

  @After
  def tearDown{
    system.shutdown()
  }

  implicit val jobContext = mock(classOf[JobContext])

  def getReceiver(s: String): ActorRef = {
    if(s=="receiver1"){
      testProbe.ref
    } else {
      assert(false)
      return null
    }
  }

  val testFSMId = "testFSM"

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
  def testNegotiatingGetMessage {
    val testFSM = TestFSMRef(new M2AActor(actionContextFactory, 1, 1, actoinId => 0))
    assert(testFSM.stateName==Death)
    testFSM ! Start
    assert(testFSM.stateName==Alive)
    testFSM ! EOC
    assert(testFSM.stateName==Negotiating)
    testFSM ! M2AMessage("HELLO")
    assert(testFSM.stateName==Negotiating)
  }

  @Test
  def testRenegotiateReply {
    val (testRef, testFSM) = negotiateSetup(2,1)
    val negotiate = Negotiate(Seq((testRefId, testRef), (testFSMId, testFSM)))
    testFSM ! negotiate
    testProbe.expectNoMsg()
    testFSM ! negotiate
    testProbe.expectNoMsg()
    testFSM ! negotiate
    testProbe.expectMsgPF()( {
     case Broadcast(Negotiate(array)) => {
       assert(array.last == (testRefId, testRef))
     }
    })
  }


  val testRefId = "testRef"

  def negotiateSetup(nmbOfNormalSenders: Int, nmbOfLoopedSenders: Int) = {
    val testFSM = TestFSMRef(new M2AActor(actionContextFactory, nmbOfNormalSenders, nmbOfLoopedSenders, actionId => nmbOfNormalSenders))
    val testRef = TestActorRef[TestActor]
    assert(testFSM.stateName == Death)
    testFSM ! Start
    testProbe.expectMsg(Broadcast(Start))
    assert(testFSM.stateName == Alive)
    (testRef, testFSM)
  }

  @Test
  def testAliveForwardNegotiate {
    val (testRef, testFSM) = negotiateSetup(2,2)
    val negotiate = Negotiate(Seq((testRefId, testRef), (testFSMId, testFSM)))
    testFSM ! negotiate
    testProbe.expectNoMsg()
    testFSM ! negotiate
    testProbe.expectNoMsg()
    testFSM ! negotiate
    testProbe.expectNoMsg()
    testFSM ! negotiate
    testProbe.expectMsgPF()( {
      case Broadcast(Negotiate(seq)) => {
        assert(seq.equals(negotiate.actors.slice(0, negotiate.actors.size -1)))
      }
    })
  }

  @Test
  def testRenegotiateSend {
    val (testRef, testFSM) = negotiateSetup(3,2)
    val negotiate = Negotiate(Seq((testRefId, testRef)))
    testFSM ! negotiate
    testProbe.expectNoMsg()
    testFSM ! negotiate
    testProbe.expectNoMsg()
    testFSM ! negotiate
    testProbe.expectMsgPF()( {
      case Broadcast(Negotiate(seq)) => {
        assert(seq.toSeq.equals(Seq((testRefId, testRef), (testFSMId, testFSM))))
      }
    })
  }

  @Test
  def testNegotiateToFlushToDeath {
    val (_, testFSM) = negotiateSetup(2,3)
    val sequence = Seq((testFSMId, testFSM))
    testFSM ! EOC
    testFSM ! EOC
    (1 to 3).foreach(ignored => testFSM ! Negotiate(sequence))
    assert(testFSM.stateName == Flushing)
    (1 to 3).foreach(ignored => testFSM ! Flush(sequence))
    assert(testFSM.stateName == NearDeath)
    (1 to 3).foreach(i => {
      testFSM ! ReadyToDie
      if (i < 3) assert(testFSM.stateName==NearDeath)
    })
    assert(testFSM.stateName == Death)
  }

}
