package eu.xenit.move2alf.pipeline.actors

import akka.actor.{ActorContext, ActorSystem}
import eu.xenit.move2alf.pipeline.state.JobContext
import org.junit.Test
import org.mockito.Mockito._
import akka.testkit.{TestActor, TestActorRef}

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
    implicit val system = ActorSystem("TestSystem2")
    implicit val jobContext = new JobContext("TestJobContext")
    val mockedReceiver = mock(classOf[TestActorRef[TestActor]])
    implicit val context = mock(classOf[ActorContext])
//    val factory = new BasicActionContextFactory(new JavaActionImpl[AbstractMessage], Map("default" -> mockedReceiver))
//    val actor = new ActionActorFactory("testActor", factory, 1, 2).createActor
  }

}