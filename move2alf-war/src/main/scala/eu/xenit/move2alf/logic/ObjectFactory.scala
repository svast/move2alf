package eu.xenit.move2alf.logic

import scala.collection.JavaConversions._
import eu.xenit.move2alf.common.LogHelper
import org.springframework.stereotype.Component
import org.springframework.context.{ApplicationContextAware, ApplicationContext}
import org.springframework.beans.factory.config.AutowireCapableBeanFactory

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/12/13
 * Time: 1:45 PM
 */
class ObjectFactory[T](clazz: Class[_], parameters: java.util.Map[String, String], private val beanFactory: AutowireCapableBeanFactory) extends LogHelper{

  def createObject(): T = {
    val constructor = clazz.getConstructor()
    val basicAction: T = constructor.newInstance().asInstanceOf[T]
    beanFactory.autowireBean(basicAction)
    val methods = clazz.getMethods
    val methodMap = methods map {
      method => (method.getName, method)
    } toMap

    parameters foreach {
      case (key, value) => {
        try {
          val method = methodMap("set"+key.capitalize)

          try {
            logger.debug("Setting parameter: "+key+", value: "+value.toString+" for clazz=" + clazz)
            method.invoke(basicAction, value)
          } catch {
            case e: IllegalArgumentException => {
              logger.error("Could not set parameter: "+key+"\n" +
                "Method parameter: "+method.getParameterTypes()(0).getCanonicalName+"\n" +
                "Value type: "+value.getClass.getCanonicalName, e)
            }
            case e: NullPointerException => {
              logger.error("NullPointer", e)
              if(method == null){
                logger.error("method is null")
              }
              logger.error("key: "+key)
              logger.error("value: "+value)
            }
          }
        } catch {
          case e: NoSuchElementException => logger.info("No setter for parameter: "+key)
        }
      }
    }
    basicAction
  }

}
