package eu.xenit.move2alf.core.action

import eu.xenit.move2alf.core.action.messages.{SetAclMessage, SetACLReply, BatchACLMessage}
import scala.collection.JavaConversions._
import eu.xenit.move2alf.repository.UploadResult
import eu.xenit.move2alf.core.simpleaction.data.FileInfo
import eu.xenit.move2alf.common.Parameters

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/20/13
 * Time: 11:35 AM
 */
class AlfrescoACLAction extends ActionWithDestination[BatchACLMessage, Boolean]{


  protected def executeImpl(message: BatchACLMessage) {

    for (acl <- message.acls){
      sendTaskToDestination(message, new SetAclMessage(acl), reply => Unit)
    }

    val inputPath = message.batch.get(0).get(Parameters.PARAM_INPUT_PATH)

    for (result <- message.uploadResultList) {
      val newParameterMap = new FileInfo()
      newParameterMap.put(Parameters.PARAM_INPUT_PATH, inputPath);
      newParameterMap.put(Parameters.PARAM_FILE, result.getDocument().file)
      newParameterMap.put(Parameters.PARAM_NAME, result.getDocument().name)
      newParameterMap.put(Parameters.PARAM_STATUS, if (result.getStatus() == UploadResult.VALUE_OK)
        Parameters.VALUE_OK else Parameters.VALUE_FAILED);
      newParameterMap.put(Parameters.PARAM_ERROR_MESSAGE, result.getMessage());
      newParameterMap.put(Parameters.PARAM_REFERENCE, result.getReference());
      sendMessage(newParameterMap)
    }
  }

  override def handleErrorReply(original: BatchACLMessage, message: AnyRef, reply: Exception){
    message match {
      case message: SetAclMessage => handleError(message, reply)
      case _ => super.handleErrorReply(original, message, reply)
    }
  }
}

object AlfrescoACLAction{

}
