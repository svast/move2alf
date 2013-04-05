package eu.xenit.move2alf.pipeline.actions


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/6/13
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */
trait SendingAction[T <: AbstractMessage, U <: AbstractMessage] extends BasicAction[T,U] {

  final protected def sendMessage(message: U){
    actor.sendMessage(message)
  }


}
