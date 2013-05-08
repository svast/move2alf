package eu.xenit.move2alf.pipeline.actors

import eu.xenit.move2alf.pipeline.actions.ActionConfig
import scala.collection.mutable
import scala.collection.JavaConversions._
import akka.actor.{ActorContext, ActorRef}
import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.actions.context.{AbstractActionContextFactory, EndActionContextFactory, BeginActionContextFactory, BasicActionContextFactory}

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

    def countSenders(config: ActionConfig) {
      config.getReceivers() foreach {
        case (_, ac) => {
          val nmbSenders = countedActionConfigs.getOrElseUpdate(ac, 0)
          countedActionConfigs.update(ac, nmbSenders + config.getNmbOfWorkers)
          countSenders(ac)
        }
      }
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
          if(nmbSenders == 0){
            new BeginActionContextFactory(config.getClazz, config.getParameters.toMap, receiversToMap)
          } else if (config.getReceivers.isEmpty) {
            nmbEndActions += config.getNmbOfWorkers
            new EndActionContextFactory(config.getClazz, config.getParameters.toMap, ("default", jobActor), nmbSenders)
          } else {
            new BasicActionContextFactory(config.getClazz, config.getParameters.toMap, receiversToMap, nmbSenders)
          }
        }
        val factory = new ActionActorFactory(config.getId, actionContextFactory, config.getNmbOfWorkers)
        actorRefs.put(config.getId, factory.createActor)
      }
    }
    makeActors(config)

    return (actorRefs.toMap, nmbEndActions)

  }

}
