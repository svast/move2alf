package eu.xenit.move2alf.core.action

import eu.xenit.move2alf.core.action.messages.{SetACLReply, BatchACLMessage}

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/20/13
 * Time: 11:35 AM
 */
class AlfrescoACLAction extends ActionWithDestination[BatchACLMessage, SetACLReply]{
  protected def executeImpl(message: BatchACLMessage) {}
}

object AlfrescoACLAction{

}
