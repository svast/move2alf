package eu.xenit.move2alf.core.action

import eu.xenit.move2alf.core.simpleaction.data.FileInfo
import eu.xenit.move2alf.core.action.messages.PutContentMessage
import eu.xenit.move2alf.common.Parameters
import java.io.File

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 7/1/13
 * Time: 3:43 PM
 */
class PutContentAction extends ActionWithDestination[FileInfo, String]{
  protected def executeImpl(message: FileInfo) {
    logger.debug("PutContent")
    sendTaskToDestination(message, new PutContentMessage(message.get(Parameters.PARAM_FILE).asInstanceOf[File],
      message.get(Parameters.PARAM_MIMETYPE).asInstanceOf[String]),
     contentUrl => {
       message.put(Parameters.PARAM_CONTENTURL, contentUrl)
       sendMessage(message)
     }
    )
  }
}
