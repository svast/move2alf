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


  @Test
  def loopBehaviorTest {
    implicit val system = ActorSystem("loopBehaviorTest")
    val testProbe = TestProbe()

    var startRef: ActorRef = null

    class TestActor(val testProbe: TestProbe) extends Actor{
      def receive: this.type#Receive = {
        case Start => {
          startRef ! Broadcast(Start)
        }
        case EOC => {
          startRef ! Broadcast(EOC)
        }
      }


      implicit val jobContext = mock(classOf[JobContext])

      val start = new ActionConfig("start", new ActionFactory {
        def createAction(): Action = return new JavaActionImpl[String]()
      }, 2)

      val end = new ActionConfig("end", new ActionFactory {
        def createAction(): Action = return new JavaActionImpl[String]()
      }, 2)

      start.addReceiver("default", end)
      end.addReceiver("loop", start)

      val pipeLineFactory = new PipeLineFactory(testProbe.ref)
      startRef = pipeLineFactory.generateActors(start)._1.get("start").get
    }

    val testActor = system.actorOf(Props(new TestActor(testProbe)))
    testActor ! Start
    testProbe.expectMsg(Broadcast(Start))
    testActor ! EOC
//    testProbe.expectMsgPF()( {
//      case Broadcast(Negotiate(Seq(el))) => {
//        assert(el._1 == "start")
//      }
//    })
//    testProbe.expectMsgPF()( {
//      case Broadcast(Flush(Seq(el))) => {
//        assert(el._1 == "start")
//      }
//    })
//    testProbe.expectMsg(Broadcast(ReadyToDie))
//    testProbe.expectMsg(Broadcast(BackAlive))
//    testProbe.expectMsg(Broadcast(EOC))

    var count = 0
    testProbe.fishForMessage()( {
      case Broadcast(EOC) => {
        //if(count == 2) true else count=count+1; false
        true
      }
      case _ => false
    })
  }

}
