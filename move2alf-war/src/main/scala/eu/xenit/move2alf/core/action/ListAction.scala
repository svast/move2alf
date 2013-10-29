package eu.xenit.move2alf.core.action

import eu.xenit.move2alf.core.simpleaction.data.FileInfo
import eu.xenit.move2alf.common.Parameters
import java.io.File
import eu.xenit.move2alf.core.action.messages.ListMessage

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 7/2/13
 * Time: 9:52 AM
 */
class ListAction extends ActionWithDestination[FileInfo, Boolean]{
  private var path: String = null
  def setPath(path: String) {
    this.path = path
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
    remotePath = remotePath.substring(0, remotePath.length - 1)
    val name: String = newParameterMap.get(Parameters.PARAM_NAME).asInstanceOf[String]

    sendTaskToDestination(fileInfo, new ListMessage(remotePath, name), result => {
      if(result) {
        fileInfo.put(Parameters.PARAM_STATUS, Parameters.VALUE_OK)
        sendMessage(fileInfo)
      } else handleError(fileInfo, "This file is not in the repository.")
    })
  }
}

object ListAction{
  val PARAM_PATH: String = "path"
}