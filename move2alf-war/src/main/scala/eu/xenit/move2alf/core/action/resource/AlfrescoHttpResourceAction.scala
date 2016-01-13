package eu.xenit.move2alf.core.action.resource

import eu.xenit.move2alf.core.action.messages._
import eu.xenit.move2alf.core.sharedresource.SharedResourceService
import eu.xenit.move2alf.core.sharedresource.alfresco.{AlfrescoHttpSharedResource}
import org.springframework.beans.factory.annotation.Autowired

/**
  * Created by Stan on 12-Jan-16.
  */
class AlfrescoHttpResourceAction extends ResourceAction[AnyRef]{
  private var alfrescoSourceSink: AlfrescoHttpSharedResource = _

  @Autowired
  private var sharedResourceService: SharedResourceService = _


  def setAlfrescoHttpSharedResource(id: String){
    alfrescoSourceSink = sharedResourceService.getSharedResource(Integer.parseInt(id)).asInstanceOf[AlfrescoHttpSharedResource]
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

object AlfrescoHttpResourceAction {
  val PARAM_ALFRESCOHTTPSHAREDRESOURCE:String  = "alfrescoHttpSharedResource"

}
