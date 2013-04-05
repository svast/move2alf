package eu.xenit.move2alf.pipeline.actions


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/6/13
 * Time: 7:56 PM
 * To change this template use File | Settings | File Templates.
 */
trait StartAction[T <: AbstractMessage, U <: AbstractMessage] extends BasicAction[T,U] {

  def execute(act: M2AActor[T,U]) {
    actor = act
    executeImpl()
  }

  def executeImpl()
}
