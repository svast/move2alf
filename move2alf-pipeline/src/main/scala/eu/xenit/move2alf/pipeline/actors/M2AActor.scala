package eu.xenit.move2alf.pipeline.actors

import akka.actor._
import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.{M2AMessage, AbstractMessage}
import eu.xenit.move2alf.pipeline.actions.SimpleAction
import eu.xenit.move2alf.common.LogHelper

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 2/28/13
 * Time: 10:05 AM
 * To change this template use File | Settings | File Templates.
 */
class M2AActor(protected val action: SimpleAction[_,_]) extends Actor with LogHelper{

  override def receive: PartialFunction[Any, Unit] = {
    action.receive
  }
}
