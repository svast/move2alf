package eu.xenit.move2alf.pipeline.actors

import eu.xenit.move2alf.pipeline.actions.{BasicAction}
import java.lang.reflect.{ParameterizedType, Type}
import scala.Function._
import akka.actor._
import eu.xenit.move2alf.pipeline.AbstractMessage
import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.actions.context.BasicActionContext

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/10/13
 * Time: 2:25 PM
 * To change this template use File | Settings | File Templates.
 */
class BasicActionActorFactory(private val actionClass: String, private val parameters: Map[String, String], receiver: ActorRef, private val nmbOfSenders: Int)(implicit context: ActorContext, jobContext: JobContext) extends AbstractActorFactory{


  def createActor: ActorRef = {
    val subClass = Class.forName(actionClass)
    val constructor = subClass.getConstructor()
    val basicAction: BasicAction[AbstractMessage,AbstractMessage] = constructor.newInstance().asInstanceOf[BasicAction[AbstractMessage,AbstractMessage]]

    parameters foreach {
      case (key, value) => subClass.getMethod("set"+key.capitalize,classOf[String]).invoke(basicAction, value)
    }
    val wrapper: BasicActionContext[AbstractMessage, AbstractMessage] = new BasicActionContext[AbstractMessage, AbstractMessage](basicAction, Map("default" -> receiver), nmbOfSenders)
    //basicAction.setContext(wrapper)

    context.actorOf(Props(new M2AActor(wrapper)))
  }


  def getGenericParamsOfSuperType(subClass: Class[_], baseClass: Class[_], mapping: Map[Type, Type] = Map()): Array[Type] = {
    val newMapping: Map[Type,Type] = subClass.getGenericSuperclass match {
      case genSuper: ParameterizedType => {
        subClass.getSuperclass.getTypeParameters.zip(genSuper.getActualTypeArguments).map(tupled(
          (typeVar, typeI) => (typeVar, mapping.get(typeI).getOrElse(typeI))
        )).toMap
      }
      case _ => mapping
    }

    val superClass = subClass.getSuperclass
    if(superClass == baseClass){
      return superClass.getTypeParameters.map(typeVar => newMapping.get(typeVar).get)
    } else {
      return getGenericParamsOfSuperType(superClass, baseClass, newMapping)
    }
  }
}
