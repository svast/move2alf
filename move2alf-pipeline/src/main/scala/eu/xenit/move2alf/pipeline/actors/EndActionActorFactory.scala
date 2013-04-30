package eu.xenit.move2alf.pipeline.actors

import akka.actor.{ActorContext, ActorRef}
import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.actions.{ReceivingAction}
import eu.xenit.move2alf.pipeline.actions.context.{EndActionContext}
import eu.xenit.move2alf.pipeline.AbstractMessage

/**
  * Created with IntelliJ IDEA.
  * User: thijs
  * Date: 4/30/13
  * Time: 10:39 AM
  * To change this template use File | Settings | File Templates.
  */
class EndActionActorFactory(actionClass: String, parameters: Map[String, AnyRef], receiver: (String, ActorRef), nmbOfSenders: Int, nmbActors: Int = 1)(implicit context: ActorContext, jobContext: JobContext) extends AbstractActionActorFactory(actionClass, parameters, nmbActors) {
   protected type T = ReceivingAction[AbstractMessage]
   protected type U = EndActionContext[AbstractMessage]

   protected def constructActionContext(basicAction: T): U = {
     val actionContext = new U(basicAction, receiver, nmbOfSenders)
     actionContext
   }
 }
