package eu.xenit.move2alf.core.action

import eu.xenit.move2alf.core.action.messages.CheckExistenceMessage
import eu.xenit.move2alf.core.simpleaction.data.FileInfo
import eu.xenit.move2alf.common.Parameters
import java.io.File

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 7/2/13
 * Time: 8:40 AM
 */
class ExistenceCheck extends ActionWithDestination[FileInfo, Boolean]{
  protected def executeImpl(fileInfo: FileInfo) {
    val newParameterMap: FileInfo = new FileInfo
    newParameterMap.putAll(fileInfo)
    val name: String = (newParameterMap.get(Parameters.PARAM_FILE).asInstanceOf[File]).getName
    sendTaskToDestination(fileInfo, new CheckExistenceMessage(name), exists => {
      if(!exists){
        handleError(fileInfo, "The file was not found in the repository.")
      } else {
        newParameterMap.put(Parameters.PARAM_STATUS, Parameters.VALUE_OK)
        sendMessage(newParameterMap)
      }
    })
  }
}
