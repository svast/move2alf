package eu.xenit.move2alf.pipeline.actors

import eu.xenit.move2alf.pipeline.actions.ActionConfig
import scala.collection.{immutable, mutable}
import scala.collection.JavaConversions._
import akka.actor.{ActorContext, ActorRef}
import eu.xenit.move2alf.pipeline.state.JobContext
import eu.xenit.move2alf.pipeline.actions.context.{EndActionContextFactory, BasicActionContextFactory}

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
    val countedActionConfigs = new mutable.HashMap[ActionConfig, (Int, Int, mutable.Map[String, Int])]()
    var nmbEndActions = 0

    countedActionConfigs.update(config, (1,0, new mutable.HashMap[String, Int]()))
    val nonEndActions = new mutable.HashSet[ActionConfig]()
    val counted = new mutable.HashSet[ActionConfig]()

    def countSenders(config:ActionConfig, loopList: immutable.Set[ActionConfig]) {
      counted += config
      config.getReceivers foreach {
        case (_, ac) => {
          if(!countedActionConfigs.contains(ac)){
            countedActionConfigs.update(ac, (0,0,new mutable.HashMap[String, Int]))
          }

          val (senders, loopedSenders, map) = countedActionConfigs.get(ac).get
          if(loopList.contains(ac)){
            loopList.foreach(sender => {
              val map = countedActionConfigs.get(ac).get._3
              val count = map.get(sender.getId).getOrElse(0)
              map.update(sender.getId, count+config.getNmbOfWorkers)
            })
            map.update(config.getId, map.get(config.getId).getOrElse(0) + config.getNmbOfWorkers)
            countedActionConfigs.update(ac, (senders, loopedSenders+config.getNmbOfWorkers, map))
          } else {
            countedActionConfigs.update(ac, (senders + config.getNmbOfWorkers,loopedSenders, map))
            nonEndActions+=config
            countSenders(ac, loopList + config)
          }
        }
      }
    }

    countSenders(config, new immutable.HashSet[ActionConfig]())

    counted foreach { config =>
      val nmbSenders = countedActionConfigs.get(config).get
      val jobActorId = "JOBACTOR" + jobContext.jobId

      def getActorRefForId(id: String) = {
        if(id == jobActorId){
          jobActor
        } else {
          actorRefs.get(config.getReceivers.get(id).getId).get
        }
      }

      val actionContextFactory = {
        if(nonEndActions.contains(config)){
          new BasicActionContextFactory(config.getId, config.getActionFactory, config.getReceivers.keySet().toSet, getActorRefForId)
        } else {
          nmbEndActions += config.getNmbOfWorkers
          new EndActionContextFactory(config.getId, config.getActionFactory, config.getReceivers.keySet().+(jobActorId).toSet, getActorRefForId)
        }
      }

      val factory = new ActionActorFactory(config.getId, actionContextFactory, nmbSenders._1, nmbSenders._2, actionId => countedActionConfigs.get(config).get._3.get(actionId).getOrElse(0), config.getNmbOfWorkers, config.getDispatcher)
      actorRefs.put(config.getId, factory.createActor)
    }

    return (actorRefs.toMap, nmbEndActions)

  }

}
