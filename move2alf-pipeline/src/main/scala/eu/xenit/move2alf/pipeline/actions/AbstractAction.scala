package eu.xenit.move2alf.pipeline.actions

import eu.xenit.move2alf.pipeline.actors.{AbstractM2AActor, M2AActor}
import eu.xenit.move2alf.pipeline.AbstractMessage


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/5/13
 * Time: 2:54 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractAction {

  type U = AbstractM2AActor

  private var _actor: U = _

  private[actions] def actor: U = _actor
  private[actions] def actor_= (value: U):Unit = _actor = value

  final protected def setStateValue(key:String, value:Any) {
    actor.setStateValue(key, value)
  }

  final protected def getStateValue(key: String): Any = {
    actor.getStateValue(key)
  }
}
