package eu.xenit.move2alf.pipeline.actions

import org.junit.{Before, Test}

import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.StringMessage
import org.mockito.Mockito._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Matchers.{eq => the, any}
import akka.actor.ActorRef


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/9/13
 * Time: 2:53 PM
 * To change this template use File | Settings | File Templates.
 */
class ActionWrapperTest extends MockitoSugar {

  var actionWrapper: ActionWrapper[_,_] = _

  @Before
  def before(){
    implicit val jobContext = new JobContext
    actionWrapper = new ActionWrapper[StringMessage, StringMessage](mock[BasicAction[StringMessage, StringMessage]], mock[ActorRef], 1)
  }

  @Test
  def testReceive(){

  }
}
