package eu.xenit.move2alf.pipeline.actors

import eu.xenit.move2alf.pipeline.actions.ActionConfig
import scala.collection.mutable
import scala.collection.JavaConversions._
import akka.actor.{ActorContext, ActorRef}
import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.actions.context.{AbstractActionContextFactory, EndActionContextFactory, BasicActionContextFactory}

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/30/13
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
class PipeLineFactory(private val jobActor: ActorRef)(implicit val context: ActorContext, implicit val jobContext: JobContext) {

  def generateActors(config: ActionConfig): (Map[String, ActorRef], Int) = {
    val actorRefs: mutable.Map[String, ActorRef] = new mutable.HashMap[String, ActorRef]()
    val countedActionConfigs = new mutable.HashMap[ActionConfig, Int]()
    var nmbEndActions = 0

    var counted = new mutable.HashSet[ActionConfig]()
    def countSenders(config: ActionConfig) {
      config.getReceivers() foreach {
        case (_, ac) => {
          val nmbSenders = countedActionConfigs.getOrElseUpdate(ac, 0)
          countedActionConfigs.update(ac, nmbSenders + config.getNmbOfWorkers)
          if (!counted.contains(ac)) countSenders(ac)
        }
      }
      counted.+=(config)
    }
    countSenders(config)

    def makeActors(config: ActionConfig) {
      config.getReceivers() foreach {
        case (_, ac) => {
          makeActors(ac)
        }
      }
      if(!actorRefs.contains(config.getId)){
        val nmbSenders = countedActionConfigs.get(config).getOrElse(0)

        def receiversToMap = {
          config.getReceivers map {
            case (key, receiver) => (key, actorRefs.get(receiver.getId).get)
          } toMap
        }

        val actionContextFactory: AbstractActionContextFactory = {
         if (config.getReceivers.isEmpty) {
            nmbEndActions += config.getNmbOfWorkers
            new EndActionContextFactory(config.getId, config.getClazz, config.getParameters.toMap, ("default", jobActor))
          } else {
            new BasicActionContextFactory(config.getId, config.getClazz, config.getParameters.toMap, receiversToMap)
          }
        }
        val factory = new ActionActorFactory(config.getId, actionContextFactory, if(nmbSenders==0) 1 else nmbSenders, config.getNmbOfWorkers)
        actorRefs.put(config.getId, factory.createActor)
      }
    }
    makeActors(config)

    return (actorRefs.toMap, nmbEndActions)

  }

}
