package eu.xenit.move2alf.pipeline.actions

import eu.xenit.move2alf.pipeline.AbstractMessage
import eu.xenit.move2alf.pipeline.actors.{SendingActor, ReceivingActor, AbstractM2AActor, M2AActor}


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/7/13
 * Time: 9:58 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class SimpleAction[T <: AbstractMessage, V <: AbstractMessage] extends AbstractAction with ReceivingAction[T] with SendingAction[V]{

}
