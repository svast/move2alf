package eu.xenit.move2alf.pipeline.actions.context

import eu.xenit.move2alf.pipeline.AbstractMessage

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/25/13
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 */
trait SendingContext[T <: AbstractMessage] extends StateContext{

  def sendMessage(message: T)

  def sendMessage(message: T, receiver: String)

}
