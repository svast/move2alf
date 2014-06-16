package eu.xenit.move2alf.pipeline.actors

import org.junit.Test
import akka.actor.{ActorContext, ActorSystem}
import eu.xenit.move2alf.pipeline.state.JobContext
import org.mockito.Mockito._
import akka.testkit.TestActorRef
import eu.xenit.move2alf.pipeline.actions._

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/2/13
 * Time: 9:41 AM
 * To change this template use File | Settings | File Templates.
 */
class PipeLineFactoryTest {

  @Test
  def testGeneratePipeLine(){
    implicit val system = ActorSystem("TestSystem4")
    implicit val jobContext = new JobContext("Testid")
    implicit val context = mock(classOf[ActorContext])

    val factory = new PipeLineFactory(mock(classOf[TestActorRef[TestActor]]))

    val startAction = new ActionConfig("startAction", mock(classOf[ActionFactory]), 1)
    val middleAction = new ActionConfig("middleAction", mock(classOf[ActionFactory]), 1)
    val endAction = new ActionConfig("endAction", mock(classOf[ActionFactory]), 1)
    startAction.addReceiver("default", middleAction)
    middleAction.addReceiver("default", endAction)

    val (actorRefs, nmbOfEndActions) = factory.generateActors(startAction)
    assert(nmbOfEndActions==1)
    assert(actorRefs.contains("startAction"))
    assert(actorRefs.size == 3)

    val endAction2 = new ActionConfig("endAction2", mock(classOf[ActionFactory]), 3)
    middleAction.addReceiver("secondEnd", endAction2)
    val (actorRefs2, nmbOfEndActions2) = factory.generateActors(startAction)
    assert(nmbOfEndActions2 == 4)
    assert(actorRefs2.size == 4)

    val middleAction2 = new ActionConfig("middleAction2", mock(classOf[ActionFactory]), 8)
    startAction.addReceiver("default2", middleAction2)
    middleAction2.addReceiver("default3", endAction)

    val (actorRefs3, nmbOfEndActions3) = factory.generateActors(startAction)
    assert(nmbOfEndActions3 == 4)
    assert(actorRefs3.size == 5)
  }

  @Test
  def testLoop(){
    implicit val system = ActorSystem("TestSystem5")
    implicit val jobContext = new JobContext("TestJobContext")
    implicit val context = mock(classOf[ActorContext])

    val factory = new PipeLineFactory(mock(classOf[TestActorRef[TestActor]]))

    val startAction = new ActionConfig("startAction", mock(classOf[ActionFactory]), 1)
    val middleAction = new ActionConfig("middleAction", mock(classOf[ActionFactory]), 1)
    val endAction = new ActionConfig("endAction", mock(classOf[ActionFactory]), 1)

    startAction.addReceiver("middle", middleAction)
    middleAction.addReceiver("end", endAction)
    endAction.addReceiver("start", startAction)

    factory.generateActors(startAction)
  }

}
