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
    val countedConnections = new mutable.HashSet[(ActionConfig, String, ActionConfig)]()
    val counted = new mutable.HashSet[ActionConfig]()

    def countSenders(config:ActionConfig, loopList: List[ActionConfig]) {
      counted += config
      config.getReceivers foreach {
        case (senderId, ac) => {
          if(!countedActionConfigs.contains(ac)){
            countedActionConfigs.update(ac, (0,0,new mutable.HashMap[String, Int]))
          }

          val (senders, loopedSenders, map) = countedActionConfigs.get(ac).get
          map.update(config.getId, map.get(config.getId).getOrElse(0) + 1)
          if(loopList.contains(ac) | ac == config){
            val map2 = countedActionConfigs.get(ac).get._3
            loopList.slice(loopList.indexOf(ac), loopList.size).foreach(sender => {
              val count = map2.get(sender.getId).getOrElse(0)
              map2.update(sender.getId, count+config.getNmbOfWorkers)
            })
            if (!countedConnections.contains((config,senderId, ac))) {
              countedActionConfigs.update(ac, (senders, loopedSenders+config.getNmbOfWorkers, map))
              countedConnections.+=((config,senderId, ac))
            }
          } else {
            if (!countedConnections.contains((config,senderId, ac))) {
              countedActionConfigs.update(ac, (senders + config.getNmbOfWorkers,loopedSenders, map))
              countedConnections.+=((config,senderId, ac))
            }
            nonEndActions+=config
            countSenders(ac, loopList :+ config)
          }
        }
      }
    }

    countSenders(config, List[ActionConfig]())


    counted foreach { config =>
      val nmbSenders = countedActionConfigs.get(config).get

      val defaultReceiver = "default"

      def getActorRefForId(id: String) = {
        actorRefs.get(config.getReceivers.get(id).getId).get
      }

      val actionContextFactory = {
        if(nonEndActions.contains(config)){
          new BasicActionContextFactory(config.getId, config.getActionFactory, config.getReceivers.keySet().toSet, getActorRefForId)
        } else {
          nmbEndActions += config.getNmbOfWorkers
          new EndActionContextFactory(config.getId, config.getActionFactory, config.getReceivers.keySet().toSet.+(defaultReceiver), receiver => if(receiver == defaultReceiver) jobActor else getActorRefForId(receiver))
        }
      }

      val factory = new ActionActorFactory(config.getId, actionContextFactory, nmbSenders._1, nmbSenders._2, actionId => countedActionConfigs.get(config).get._3.get(actionId).getOrElse(nmbSenders._1), config.getNmbOfWorkers, config.getDispatcher)
      actorRefs.put(config.getId, factory.createActor)
    }

    return (actorRefs.toMap, nmbEndActions)

  }

}
