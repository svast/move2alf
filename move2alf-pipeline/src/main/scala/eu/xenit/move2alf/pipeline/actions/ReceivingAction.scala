package eu.xenit.move2alf.pipeline.actions


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/6/13
 * Time: 8:06 PM
 * To change this template use File | Settings | File Templates.
 */
trait ReceivingAction[T <: AbstractMessage, U <: AbstractMessage] extends BasicAction[T,U] {

  final def execute(message: T, actor: M2AActor[T,U]){
    this.actor = actor
    executeImpl(message)
  }

  def executeImpl(message: T)

  final protected def initCounter(key: String, init: Int) {
    actor.initCounter(key, init)
  }

}
