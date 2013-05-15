package eu.xenit.move2alf.pipeline.state

import concurrent.stm._

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/5/13
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
class JobContext(val jobId: String) {

  private val states = TMap.empty[String, Any]

  def getStateValue(key: String): Any =  {
    return states.single.get(key).get
  }

  def setStateValue(key: String, value: Any) = {
    states.single += key -> value
  }

  def reset() = {
    states.single.clear()
  }
}
