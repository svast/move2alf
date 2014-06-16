package eu.xenit.move2alf.pipeline.actions.context

import akka.actor.{ActorRef, ActorContext}

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/25/13
 * Time: 4:18 PM
 * To change this template use File | Settings | File Templates.
 */
class StateContextImpl(private val ct: AbstractActionContext) extends StateContext{

  def setStateValue(key: String, value: Any) {
    ct.setStateValue(key, value)
  }

  def getStateValue(key: String): Any = {
    ct.getStateValue(key)
  }

  def getJobId:String = {
    ct.getJobId
  }


  def getActionId(): String = {
    ct.id
  }

  def getActorContext(): ActorContext = {
    ct.context
  }

  def getActorRef(): ActorRef = {
    ct.context.self
  }

  def getRouterActorRef(): ActorRef = {
    ct.context.parent
  }
}
