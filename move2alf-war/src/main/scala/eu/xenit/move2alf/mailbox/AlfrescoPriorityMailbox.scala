package eu.xenit.move2alf.mailbox

import akka.dispatch.{PriorityGenerator, UnboundedPriorityMailbox}
import akka.actor.ActorSystem
import com.typesafe.config.Config
import eu.xenit.move2alf.core.action.messages._
import eu.xenit.move2alf.pipeline.{EOC, M2AMessage}
import eu.xenit.move2alf.pipeline.actors.{ReadyToDie, Negotiate, Flush}

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 7/30/13
 * Time: 1:57 PM
 */
class AlfrescoPriorityMailbox(settings: ActorSystem.Settings, config: Config) extends UnboundedPriorityMailbox(
  PriorityGenerator {
    case M2AMessage(m) => {
      m match {
        case m:SendBatchMessage => 0
        case m:DeleteMessage => 0
        case m:BatchACLMessage => 0
        case m:ListMessage => 0
        case m:CheckExistenceMessage => 0
        case _ => 1
      }
    }
    case Negotiate(_) => 2
    case Flush(_) => 3
    case ReadyToDie => 4
    case EOC => 5
    case _ => 1

  }
)


