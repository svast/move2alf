package eu.xenit.move2alf.pipeline.actions.context

import akka.actor.{ActorContext, ActorRef}
import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.actions.{Action, ActionFactory}

/**
  * Created with IntelliJ IDEA.
  * User: thijs
  * Date: 4/30/13
  * Time: 10:39 AM
  * To change this template use File | Settings | File Templates.
  */
class EndActionContextFactory(id: String, actionFactory: ActionFactory, receiver: (String, ActorRef))(implicit jobContext: JobContext) extends AbstractActionContextFactory(id, actionFactory) {

   protected def constructActionContext(basicAction: Action)(implicit context: ActorContext) = {
//     logger.debug("Constructing EndAction")
     val actionContext = new AbstractActionContext(id, Map(receiver)){
       val action = basicAction
     }
     actionContext
   }
 }
