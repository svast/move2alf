package eu.xenit.move2alf.pipeline.actions


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/7/13
 * Time: 9:58 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class SimpleAction[T <: AbstractMessage, U <: AbstractMessage] extends BasicAction[T,U] with SendingAction[T,U] with ReceivingAction[T,U]{

}
