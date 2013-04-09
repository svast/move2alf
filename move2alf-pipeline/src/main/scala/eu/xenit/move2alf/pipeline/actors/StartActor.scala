package eu.xenit.move2alf.pipeline.actors

import eu.xenit.move2alf.pipeline.{Start, AbstractMessage}
import eu.xenit.move2alf.pipeline.actions.StartAction

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/8/13
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
trait StartActor[T <: AbstractMessage] extends SendingActor[T]{

  override def receive = {
    case Start => execute()
    case s => super.receive(s)
  }

  protected def execute(){
    action.asInstanceOf[StartAction[T]].execute(this)
  }
}
