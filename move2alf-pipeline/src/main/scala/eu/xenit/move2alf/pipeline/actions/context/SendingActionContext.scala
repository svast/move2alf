package eu.xenit.move2alf.pipeline.actions.context

import eu.xenit.move2alf.pipeline.{M2AMessage, AbstractMessage}

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/6/13
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */
trait SendingActionContext extends AbstractActionContext {

  final def sendMessage(message: AbstractMessage, receiver: String = "default"){
    receivers.get(receiver).get ! M2AMessage(message)
  }
}
