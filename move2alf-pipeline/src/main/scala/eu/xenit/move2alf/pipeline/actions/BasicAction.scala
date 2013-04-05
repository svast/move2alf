package eu.xenit.move2alf.pipeline.actions

import eu.xenit.move2alf.pipeline.actors.M2AActor
import eu.xenit.move2alf.pipeline.AbstractMessage


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/5/13
 * Time: 2:54 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractAction {

  private[actions] var actor: AbstractM2AActor = _

  final protected def setStateValue(key:String, value:Any) {
    actor.setStateValue(key, value)
  }

  final protected def getStateValue(key: String): Any = {
    actor.getStateValue(key)
  }
}
