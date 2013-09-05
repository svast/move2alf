package eu.xenit.move2alf.core.action.resource

import eu.xenit.move2alf.core.action.messages._
import eu.xenit.move2alf.core.sourcesink.AlfrescoSourceSink
import org.springframework.beans.factory.annotation.Autowired
import eu.xenit.move2alf.core.sharedresource.SharedResourceService

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 7/1/13
 * Time: 1:40 PM
 */
class AlfrescoResourceAction extends ResourceAction[AlfrescoMessage]{

  private var alfrescoSourceSink: AlfrescoSourceSink = _

  @Autowired
  private var sharedResourceService: SharedResourceService = _


  def setAlfrescoSourceSink(id: String){
    alfrescoSourceSink = sharedResourceService.getSharedResource(Integer.parseInt(id)).asInstanceOf[AlfrescoSourceSink]
  }

  protected def executeImpl(message: AlfrescoMessage) {
    message match {
      case m:SendBatchMessage => reply(new BatchReply(alfrescoSourceSink.sendBatch(m.writeOption, m.documentsToUpload)))
      case m:PutContentMessage => reply(alfrescoSourceSink.putContent(m.file, m.mimeType))
      case m:CheckExistenceMessage => reply(boolean2Boolean(alfrescoSourceSink.fileNameExists(m.fileName)))
      case m:ListMessage => reply(boolean2Boolean(alfrescoSourceSink.exists(m.remotePath, m.name)))
      case m: ValidateMessage => reply(boolean2Boolean(alfrescoSourceSink.validate()))
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
  val PARAM_ALFRESCOSOURCESINK:String  = "alfrescoSourceSink";
}
