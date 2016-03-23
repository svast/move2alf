package eu.xenit.move2alf.core.action.resource

import eu.xenit.move2alf.core.action.messages.PutContentMessage
import eu.xenit.move2alf.core.sharedresource.castor.CastorSharedResource
import org.springframework.beans.factory.annotation.Autowired
import eu.xenit.move2alf.core.sharedresource.SharedResourceService

/**
 * Actor forwarding move2alf messages destined to Castor
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 9/10/13
 * Time: 3:16 PM
 */
class CastorResourceAction extends ResourceAction[PutContentMessage] {

  @Autowired
  private var sharedResourceService: SharedResourceService = _

  private var castorSharedResource: CastorSharedResource = _
  def setCastorSharedResource(id: String){
    this.castorSharedResource = sharedResourceService.getSharedResource(Integer.parseInt(id)).asInstanceOf[CastorSharedResource]
  }

  protected def executeImpl(message: PutContentMessage) {
    reply(castorSharedResource.uploadFile(message.file, message.mimeType))
  }
}

object CastorResourceAction {
  val PARAM_CASTORSHAREDRESOURCE:String  = "castorSharedResource"
}

