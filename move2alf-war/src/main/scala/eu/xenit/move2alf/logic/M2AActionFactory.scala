package eu.xenit.move2alf.logic

import eu.xenit.move2alf.pipeline.actions.{Action, ActionFactory}
import eu.xenit.move2alf.common.LogHelper

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/12/13
 * Time: 1:31 PM
 */
class M2AActionFactory(clazz: Class[_], parameters: java.util.Map[String, String]) extends ObjectFactory[Action](clazz, parameters) with ActionFactory with LogHelper{

  def createAction(): Action = {
    createObject()
  }
}
