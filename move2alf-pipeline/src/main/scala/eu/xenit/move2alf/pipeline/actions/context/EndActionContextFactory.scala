package eu.xenit.move2alf.pipeline.actions.context

import akka.actor.{ActorContext, ActorRef}
import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.actions.ReceivingAction
import eu.xenit.move2alf.pipeline.AbstractMessage

/**
  * Created with IntelliJ IDEA.
  * User: thijs
  * Date: 4/30/13
  * Time: 10:39 AM
  * To change this template use File | Settings | File Templates.
  */
class EndActionContextFactory(actionClass: Class[_], parameters: Map[String, AnyRef], receiver: (String, ActorRef), nmbOfSenders: Int)(implicit context: ActorContext, jobContext: JobContext) extends AbstractActionContextFactory(actionClass, parameters) {
   protected type T = ReceivingAction[AbstractMessage]

   protected def constructActionContext(basicAction: T) = {
     val actionContext = new AbstractActionContext(Map(receiver), nmbOfSenders) with StateActionContext with ReceivingActionContext[AbstractMessage] with EOCBlockingActionContext{
       val action = basicAction
     }
     actionContext
   }
 }
