package eu.xenit.move2alf.pipeline.actions.context

import akka.actor.{ActorContext, ActorRef}
import eu.xenit.move2alf.pipeline.state.JobContext

/**
  * Created with IntelliJ IDEA.
  * User: thijs
  * Date: 4/30/13
  * Time: 10:39 AM
  * To change this template use File | Settings | File Templates.
  */
class EndActionContextFactory(id: String, actionClass: Class[_], parameters: Map[String, AnyRef], receiver: (String, ActorRef))(implicit jobContext: JobContext) extends AbstractActionContextFactory(id, actionClass, parameters) {
   protected type T = AnyRef

   protected def constructActionContext(basicAction: T)(implicit context: ActorContext) = {
     logger.debug("Constructing EndAction")
     val actionContext = new AbstractActionContext(id, Map(receiver)){
       val action = basicAction
     }
     actionContext
   }
 }
