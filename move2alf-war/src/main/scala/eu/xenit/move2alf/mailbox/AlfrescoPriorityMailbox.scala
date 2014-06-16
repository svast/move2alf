package eu.xenit.move2alf.mailbox

import akka.dispatch.{PriorityGenerator, UnboundedPriorityMailbox}
import akka.actor.ActorSystem
import com.typesafe.config.Config
import eu.xenit.move2alf.core.action.messages._
import eu.xenit.move2alf.pipeline.{TaskMessage, EOC, M2AMessage}
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
        case n:SendBatchMessage => 0
        case n:DeleteMessage => 0
        case n:BatchACLMessage => 0
        case n:ListMessage => 0
        case n:CheckExistenceMessage => 0
        case _ => 1
      }
    }
    case TaskMessage(_,m,_) => {
      m match {
        case n:SendBatchMessage => 0
        case n:DeleteMessage => 0
        case n:BatchACLMessage => 0
        case n:SetAclMessage => 0
        case n:ListMessage => 0
        case n:CheckExistenceMessage => 0
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


