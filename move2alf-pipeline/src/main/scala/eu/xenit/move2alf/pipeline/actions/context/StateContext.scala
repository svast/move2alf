package eu.xenit.move2alf.pipeline.actions.context

import akka.actor.{ActorRef, ActorContext}

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/25/13
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
trait StateContext {

  def getJobId(): String

  def getActorContext(): ActorContext

  def getActorRef(): ActorRef

  def getRouterActorRef(): ActorRef

  def getActionId(): String

  /**
   * Save a variable that can be retrieved later in this cycle by an Action in the current Job.
   * @param key The key that will be used to retrieve the variable later.
   * @param value  The variable to save.
   */
  def setStateValue(key: String, value: Any)

  /**
   * Retrieve a variable.
   * @param key Key under which the variable was saved.
   * @return  The state value.
   */
  def getStateValue(key: String): Any

}
