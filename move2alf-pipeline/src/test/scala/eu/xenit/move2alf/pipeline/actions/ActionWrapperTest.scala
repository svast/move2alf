package eu.xenit.move2alf.pipeline.actions

import org.junit.{Before, Test}

import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.{M2AMessage, EOC, StringMessage}
import org.mockito.Mockito._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Matchers.{eq => the, any}
import akka.actor.{Actor, Props, ActorSystem, ActorRef}
import akka.testkit.{TestActor, TestActorRef, TestProbe}
import akka.routing.Broadcast
import eu.xenit.move2alf.pipeline.M2AMessage
import akka.routing.Broadcast
import eu.xenit.move2alf.pipeline.actions.context.BasicActionContext


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/9/13
 * Time: 2:53 PM
 * To change this template use File | Settings | File Templates.
 */
class ActionWrapperTest {

  var actionWrapper: BasicActionContext[StringMessage] = _
  val nmbReceivers = 3
  var mockedReceiver: TestActorRef[TestActor] = _
  var action: ReceivingAction[StringMessage] = _

  @Before
  def before(){
    implicit val system = ActorSystem("Test")
    implicit val jobContext = new JobContext
    mockedReceiver = mock(classOf[TestActorRef[TestActor]])
    action = mock(classOf[ReceivingAction[StringMessage]])
    actionWrapper = new BasicActionContext[StringMessage](action , Map("default" -> mockedReceiver), nmbReceivers)
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
    actionWrapper.receive(M2AMessage(message))
    //verify(action, times(1)).execute(message)
  }

  @Test
  def testSendMessage(){
    val message = new StringMessage("Test")
    actionWrapper.sendMessage(message)
    verify(mockedReceiver, times(1)) ! M2AMessage(message)
  }
}
