package eu.xenit.move2alf.pipeline.actors

import akka.actor.{ActorContext, Actor, Props, ActorSystem}
import eu.xenit.move2alf.pipeline.state.JobContext
import org.junit.Test
import org.mockito._
import org.mockito.Mockito._
import akka.testkit.{TestActor, TestActorRef}
import eu.xenit.move2alf.pipeline.actions.context.BasicActionContextFactory
import eu.xenit.move2alf.pipeline.actions.JavaActionImpl

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/10/13
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
class ActionActorFactoryTest {

  @Test
  def testBasicActionActorFactory(){
    implicit val system = ActorSystem("Test")
    implicit val jobContext = new JobContext
    val mockedReceiver = mock(classOf[TestActorRef[TestActor]])
    implicit val context = mock(classOf[ActorContext])
    val factory = new BasicActionContextFactory(classOf[JavaActionImpl[_]], Map("param1"->"Test123"), Map("default" -> mockedReceiver), 1)
    val actor = new ActionActorFactory(factory, 2)
  }

}