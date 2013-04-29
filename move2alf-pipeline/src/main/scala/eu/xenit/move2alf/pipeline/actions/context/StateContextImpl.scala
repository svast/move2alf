package eu.xenit.move2alf.pipeline.actions.context

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/25/13
 * Time: 4:18 PM
 * To change this template use File | Settings | File Templates.
 */
class StateContextImpl(private val ct: StateActionContext) extends StateContext{

  def setStateValue(key: String, value: Any) {
    ct.setStateValue(key, value)
  }

  def getStateValue(key: String): Any = {
    ct.getStateValue(key)
  }
}
