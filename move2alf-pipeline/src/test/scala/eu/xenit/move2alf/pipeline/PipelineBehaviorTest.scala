package eu.xenit.move2alf.pipeline

import org.junit.Test
import akka.actor._
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import eu.xenit.move2alf.pipeline.actors._
import eu.xenit.move2alf.pipeline.actions.{JavaActionImpl, Action, ActionFactory, ActionConfig}
import org.mockito.Mockito._
import eu.xenit.move2alf.pipeline.state.JobContext
import akka.routing.Broadcast
import org.scalatest.{BeforeAndAfterAll, WordSpecLike, Matchers}
import eu.xenit.move2alf.pipeline.actors.Negotiate
import eu.xenit.move2alf.pipeline.actors.Flush
import akka.routing.Broadcast
import scala.concurrent.duration.Duration
import scala.concurrent.duration


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 2/17/14
 * Time: 5:36 PM
 * To change this template use File | Settings | File Templates.
 */
class PipelineBehaviorTest{

  class TestActor(val setupMethod: (ActorContext) => ActorRef) extends Actor{
    def receive: this.type#Receive = {
      case Start => {
        startRef ! Broadcast(Start)
      }
      case EOC => {
        startRef ! Broadcast(EOC)
      }
    }


    val startRef: ActorRef = setupMethod(context)



  }


  @Test
  def loopBehaviorTest {
    implicit val system = ActorSystem("loopBehaviorTest")
    val testProbe = TestProbe()

    def setupMethod()(context: ActorContext): ActorRef = {
      implicit val jobContext = mock(classOf[JobContext])

      val start = new ActionConfig("start", new ActionFactory {
        def createAction(): Action = return new JavaActionImpl[String]()
      }, 2)

      val end = new ActionConfig("end", new ActionFactory {
        def createAction(): Action = return new JavaActionImpl[String]()
      }, 2)

      start.addReceiver("default", end)
      end.addReceiver("loop", start)

      val pipeLineFactory = new PipeLineFactory(testProbe.ref)(context, jobContext)
      pipeLineFactory.generateActors(start)._1.get("start").get
    }

    val testActor = system.actorOf(Props(new TestActor(setupMethod())))
    testActor ! Start
    testProbe.expectMsg(Broadcast(Start))
    testActor ! EOC

   checkForEOC(testProbe, 2)
    system.shutdown()
  }

  @Test
  def doubleLoopTest {
    implicit val system = ActorSystem("doubleLoopBehaviorTest")
    val testProbe = TestProbe()

    def setupMethod()(context: ActorContext): ActorRef = {
      implicit val jobContext = mock(classOf[JobContext])

      val start = new ActionConfig("start", new ActionFactory {
        def createAction(): Action = return new JavaActionImpl[String]()
      }, 2)

      val middle = new ActionConfig("middle", new ActionFactory {
        def createAction(): Action = return new JavaActionImpl[String]()
      }, 1)

      val end = new ActionConfig("end", new ActionFactory {
        def createAction(): Action = return new JavaActionImpl[String]()
      }, 2)

      start.addReceiver("default", middle)
      middle.addReceiver("default", end)
      end.addReceiver("loop1", start)
      middle.addReceiver("loop2", start)

      val pipeLineFactory = new PipeLineFactory(testProbe.ref)(context, jobContext)
      pipeLineFactory.generateActors(start)._1.get("start").get
    }

    val testActor = system.actorOf(Props(new TestActor(setupMethod())))
    testActor ! Start
    testActor ! EOC

    checkForEOC(testProbe, 2)
    system.shutdown()

  }


  def checkForEOC(testProbe: TestProbe, amount: Int) {
    var count: Int = 1
    testProbe.fishForMessage(Duration(9999, duration.SECONDS))({
      case Broadcast(EOC) => {
        if (count == amount) {
          true
        } else {
          count = count + 1
          false
        }
      }
      case _ => false
    })
  }

  @Test
  def selfLoopTest {
    implicit val system = ActorSystem("selfLoopTestSystem")
    val testProbe = TestProbe()

    def setupMethod()(context:ActorContext): ActorRef = {
      implicit val jobContext = mock(classOf[JobContext])
      val start = new ActionConfig("start", getActionFactory, 1)

      start.addReceiver("loop", start)
      new PipeLineFactory(testProbe.ref)(context, jobContext).generateActors(start)._1.get("start").get
    }

    val testActor = system.actorOf(Props(new TestActor(setupMethod())))
    testActor ! Start
    testActor ! EOC

    checkForEOC(testProbe, 1)
    system.shutdown()
  }


  def getActionFactory: ActionFactory with Object {def createAction(): Action} = {
    new ActionFactory {
      def createAction(): Action = new JavaActionImpl[String]
    }
  }

  @Test
  def otherDoubleLoopTest {
    implicit val system = ActorSystem("otherDoubleSelfLoopSystem")
    val testProbe = TestProbe()

    def setupMethod()(context:ActorContext): ActorRef = {
      implicit val jobContext = mock(classOf[JobContext])
      val start = new ActionConfig("start", getActionFactory, 2)
      val middle = new ActionConfig("middle", getActionFactory, 1)
      val end = new ActionConfig("end", getActionFactory, 2)

      start.addReceiver("default", middle)
      middle.addReceiver("default", end)
      middle.addReceiver("loop", start)
      end.addReceiver("loop", middle)

      new PipeLineFactory(testProbe.ref)(context,jobContext).generateActors(start)._1.get("start").get
    }

    val testActor = system.actorOf(Props(new TestActor(setupMethod())))
    testActor ! Start
    testActor ! EOC

    checkForEOC(testProbe, 2)
    system.shutdown()
  }

}
