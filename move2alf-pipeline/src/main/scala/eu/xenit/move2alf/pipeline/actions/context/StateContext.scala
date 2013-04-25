package eu.xenit.move2alf.pipeline.actions.context

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/25/13
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
trait StateContext {

  def setStateValue(key: String, value: Any)
  def getStateValue(key: String): Any

}
