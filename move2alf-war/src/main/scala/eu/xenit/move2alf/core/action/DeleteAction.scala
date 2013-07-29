package eu.xenit.move2alf.core.action

import eu.xenit.move2alf.core.simpleaction.data.FileInfo
import eu.xenit.move2alf.core.sourcesink.DeleteOption
import eu.xenit.move2alf.common.Parameters
import java.io.File
import eu.xenit.move2alf.core.action.messages.DeleteMessage

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 7/2/13
 * Time: 8:59 AM
 */
class DeleteAction extends ActionWithDestination[FileInfo, Boolean] {
  private var path: String = null
  def setPath(path: String) {
    this.path = path
  }

  private var deleteOption: DeleteOption = null
  def setDeleteOption(deleteOption: DeleteOption) {
    this.deleteOption = deleteOption
  }


  protected def executeImpl(fileInfo: FileInfo) {
    val newParameterMap: FileInfo = new FileInfo
    newParameterMap.putAll(fileInfo)
    var basePath: String = path

    if (!basePath.endsWith("/")) {
      basePath = basePath + "/"
    }

    if (!basePath.startsWith("/")) {
      basePath = "/" + basePath
    }

    var relativePath: String = newParameterMap.get(Parameters.PARAM_RELATIVE_PATH).asInstanceOf[String]
    relativePath = relativePath.replace("\\", "/")

    if (relativePath.startsWith("/")) {
      relativePath = relativePath.substring(1)
    }

    // add "cm:" in front of each path component
    var remotePath: String = basePath + relativePath
    val components: Array[String] = remotePath.split("/")
    remotePath = ""
    for (component <- components) {
      if ("" == component) {
        remotePath += "/"
      }
      else if (component.startsWith("cm:")) {
        remotePath += component + "/"
      }
      else {
        remotePath += "cm:" + component + "/"
      }
    }

    if (remotePath.length > 0) {
      remotePath = remotePath.substring(0, remotePath.length - 1)
    }

    val name: String = (newParameterMap.get(Parameters.PARAM_FILE).asInstanceOf[File]).getName

    sendTaskToDestination(new DeleteMessage(remotePath, name, deleteOption), reply => {
      if(reply) sendMessage(newParameterMap)
    })
  }
}

object DeleteAction {
  final val PARAM_PATH: String = "path"
  final val PARAM_DELETEOPTION: String = "deleteOption"
}