package eu.xenit.move2alf.pipeline.actions.context

import eu.xenit.move2alf.pipeline.AbstractMessage

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/25/13
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 */
trait SendingContext{

  def sendMessage(message: AbstractMessage)

  def sendMessage(message: AbstractMessage, receiver: String)

}
