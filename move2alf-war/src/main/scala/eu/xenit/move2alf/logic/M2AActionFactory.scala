package eu.xenit.move2alf.logic

import eu.xenit.move2alf.pipeline.actions.{Action, ActionFactory}
import eu.xenit.move2alf.common.LogHelper
import org.springframework.beans.factory.config.AutowireCapableBeanFactory

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/12/13
 * Time: 1:31 PM
 */
class M2AActionFactory(clazz: Class[_], parameters: java.util.Map[String, String], beanFactory: AutowireCapableBeanFactory) extends ObjectFactory[Action](clazz, parameters, beanFactory) with ActionFactory with LogHelper{

  def createAction(): Action = {
    createObject()
  }
}
