package eu.xenit.move2alf.pipeline.actions.context

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/26/13
 * Time: 12:08 PM
 * To change this template use File | Settings | File Templates.
 */
trait StateActionContext extends AbstractActionContext{

  /**
    * Save a variable that can be retrieved later in this cycle by an Action in the current Job.
    * @param key The key that will be used to retrieve the variable later.
    * @param value  The variable to save.
    */
  final def setStateValue(key:String, value:Any) {
    jobContext.setStateValue(key, value)
  }

  /**
   * Retrieve a variable.
   * @param key Key under which the variable was saved.
   * @return  The state value.
   */
  final def getStateValue(key: String): Any = {
    jobContext.getStateValue(key)
  }

}
