package eu.xenit.move2alf.mailbox

import akka.dispatch.{PriorityGenerator, UnboundedPriorityMailbox}
import akka.actor.ActorSystem
import com.typesafe.config.Config
import eu.xenit.move2alf.core.action.messages._

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 7/30/13
 * Time: 1:57 PM
 */
class AlfrescoPriorityMailbox(settings: ActorSystem.Settings, config: Config) extends UnboundedPriorityMailbox(
  PriorityGenerator {
    case m:SendBatchMessage => 0
    case m:DeleteMessage => 0
    case m:BatchACLMessage => 0
    case m:ListMessage => 0
    case m:CheckExistenceMessage => 0
    case _ => 1
  }
)


