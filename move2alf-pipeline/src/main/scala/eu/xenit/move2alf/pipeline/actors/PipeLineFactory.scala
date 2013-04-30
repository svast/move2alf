package eu.xenit.move2alf.pipeline.actors

import eu.xenit.move2alf.pipeline.actions.ActionConfig
import scala.collection.mutable
import scala.collection.JavaConversions._
import akka.actor.{ActorContext, ActorRef}
import eu.xenit.move2alf.pipeline.state.JobContext

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
        ac => {
          val nmbSenders = countedActionConfigs.getOrElseUpdate(ac, 0)
          countedActionConfigs.update(ac, nmbSenders + 1)
          countSenders(ac)
        }
      }
    }
    countSenders(config)

    def makeActors(config: ActionConfig) {
      config.getReceivers() foreach {
        ac => {
          makeActors(ac)
        }
      }
      if(!actorRefs.contains(config.getId)){
        val nmbSenders = countedActionConfigs.get(config).get

        def receiversToMap = {
          config.getReceivers map {
            receiver => (receiver.getId, actorRefs.get(receiver.getId).get)
          } toMap
        }

        val factory = {
          if(nmbSenders == 0){
            new BeginActionActorFactory(config.getClazz, config.getParameters.toMap, receiversToMap, config.getNmbOfWorkers)
          } else if (config.getReceivers.isEmpty) {
            new EndActionActorFactory(config.getClazz, config.getParameters.toMap, ("default", jobActor), nmbSenders, config.getNmbOfWorkers)
            nmbEndActions += 1
          } else {
            new BasicActionActorFactory(config.getClazz, config.getParameters.toMap, receiversToMap, nmbSenders, config.getNmbOfWorkers)
          }
        }

        actorRefs.put(config.getId, factory.createActor)
      }
    }
    makeActors(config)

    return (actorRefs.toMap, nmbEndActions)

  }

}
