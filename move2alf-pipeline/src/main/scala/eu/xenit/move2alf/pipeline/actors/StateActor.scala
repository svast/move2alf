package eu.xenit.move2alf.pipeline.actors


/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 2/28/13
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
trait StateActor extends PipelineActor{

  def getStateValue(key: String): Any =  {
    return jobContext getStateValue key
  }

  def setStateValue(key: String, value: Any) = {
    jobContext.setStateValue(key,value)
  }

}
