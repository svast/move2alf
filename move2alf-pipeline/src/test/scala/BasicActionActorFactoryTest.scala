import akka.actor.{Actor, Props, ActorSystem}
import eu.xenit.move2alf.pipeline.actors.BasicActionActorFactory
import eu.xenit.move2alf.pipeline.Start
import eu.xenit.move2alf.pipeline.state.JobContext
import org.junit.Test
import org.mockito._

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/10/13
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
class BasicActionActorFactoryTest {

  @Test
  def testBasicActionActorFactory(){

    val actor = ActorSystem("Test").actorOf(Props(new TestActor))
    actor ! Start
    Thread.sleep(5000)
  }

}

class TestActor extends Actor {
  implicit val jobContext = new JobContext
  val factory = new BasicActionActorFactory("eu.xenit.move2alf.pipeline.actions.ActionImpl2", Map("param1"->"Test123"), null, 1)
  val actor = factory.createActor

  def receive = {
    case Start => println("bla")
  }
}
