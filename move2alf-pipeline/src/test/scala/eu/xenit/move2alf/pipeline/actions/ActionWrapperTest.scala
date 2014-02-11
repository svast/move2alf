package eu.xenit.move2alf.pipeline.actions

import org.junit.{Ignore, After, Before, Test}

import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.{M2AMessage, EOC, StringMessage}
import org.mockito.Mockito._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Matchers.{eq => the, any}
import akka.actor._
import akka.testkit.{TestActor, TestActorRef, TestProbe}
import akka.routing.Broadcast
import eu.xenit.move2alf.pipeline.M2AMessage
import akka.routing.Broadcast
import eu.xenit.move2alf.pipeline.actions.context.{AbstractActionContext}
import eu.xenit.move2alf.pipeline.M2AMessage
import akka.routing.Broadcast


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/9/13
 * Time: 2:53 PM
 * To change this template use File | Settings | File Templates.
 */
@Ignore
class ActionWrapperTest {

  var actionWrapper: AbstractActionContext= _
  val nmbReceivers = 3
  var mockedReceiver: TestActorRef[TestActor] = _
  var _action: ReceivingAction[StringMessage] = _
  implicit var system: ActorSystem = _

  @Before
  def before(){
    system = ActorSystem("TestSystem1")
    implicit val jobContext = new JobContext("TestJobContext")
    mockedReceiver = mock(classOf[TestActorRef[TestActor]])
    _action = mock(classOf[ReceivingAction[StringMessage]])
    implicit val context = mock(classOf[ActorContext])
    actionWrapper = new AbstractActionContext("actionid",Set("default"), (s: String) => mockedReceiver){
      val action = _action
    }
  }

  @After
  def after(){
    system.shutdown()
  }

  @Test
  def testReceive(){
    //Test EOC
    val nmbTimes = 9
    1 to (nmbReceivers * nmbTimes + nmbReceivers - 1) foreach { _ => actionWrapper.receive(EOC)}
    verify(mockedReceiver, times(nmbTimes)) ! Broadcast(EOC)
    actionWrapper.receive(EOC)
    verify(mockedReceiver, times(nmbTimes+1)) ! Broadcast(EOC)

    //Test execution invocation of BasicAction
    val message = new StringMessage("Test")
    actionWrapper.receive(message)
    //verify(action, times(1)).execute(message)
  }

  @Test
  def testSendMessage(){
    val message = new StringMessage("Test")
    actionWrapper.sendMessage(message)
    verify(mockedReceiver, times(1)) ! M2AMessage(message)
  }
}
