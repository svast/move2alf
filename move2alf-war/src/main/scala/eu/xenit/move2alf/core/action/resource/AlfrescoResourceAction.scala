package eu.xenit.move2alf.core.action.resource

import eu.xenit.move2alf.core.action.messages._
import org.springframework.beans.factory.annotation.Autowired
import eu.xenit.move2alf.core.sharedresource.SharedResourceService
import eu.xenit.move2alf.core.sharedresource.alfresco.AlfrescoSharedResource

/**
 * Action forwarding move2alf messages destined to alfresco
 * This action runs in the destination job, not in the actual move2alf job
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 7/1/13
 * Time: 1:40 PM
 */
class AlfrescoResourceAction extends ResourceAction[AnyRef]{

  private var alfrescoSourceSink: AlfrescoSharedResource = _

  @Autowired
  private var sharedResourceService: SharedResourceService = _


  def setAlfrescoSharedResource(id: String){
    alfrescoSourceSink = sharedResourceService.getSharedResource(Integer.parseInt(id)).asInstanceOf[AlfrescoSharedResource]
  }

  protected def executeImpl(message: AnyRef) {
    message match {
      case m:SendBatchMessage => reply(new BatchReply(alfrescoSourceSink.sendBatch(m.writeOption, m.documentsToUpload)))
      case m:PutContentMessage => reply(alfrescoSourceSink.putContent(m.file, m.mimeType))
      case m:CheckExistenceMessage => reply(boolean2Boolean(alfrescoSourceSink.fileNameExists(m.fileName)))
      case m:ListMessage => reply(boolean2Boolean(alfrescoSourceSink.exists(m.remotePath, m.name)))
      case m:ValidateMessage => reply(boolean2Boolean(alfrescoSourceSink.validate()))
      case m:SetAclMessage => {
        alfrescoSourceSink.setACL(m.acl)
        reply(boolean2Boolean(true))
      }
      case m:DeleteMessage => {
        alfrescoSourceSink.delete(m.remotePath, m.name, m.deleteOption)
        reply(boolean2Boolean(true))
      }
    }
  }
}

object AlfrescoResourceAction {
  val PARAM_ALFRESCOSHAREDRESOURCE:String  = "alfrescoSharedResource"
}
