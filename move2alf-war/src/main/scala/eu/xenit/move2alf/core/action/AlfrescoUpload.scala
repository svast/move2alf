package eu.xenit.move2alf.core.action

import eu.xenit.move2alf.core.simpleaction.data.{FileInfo, Batch}
import eu.xenit.move2alf.core.action.messages._
import eu.xenit.move2alf.common.{Util, Parameters}
import eu.xenit.move2alf.core.sourcesink.{WriteOption, ACL}
import java.util
import scala.collection.JavaConversions._
import scala.Boolean
import java.io.File
import eu.xenit.move2alf.repository.alfresco.ws.Document
import scala.collection.immutable.Map

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/20/13
 * Time: 10:29 AM
 */
class AlfrescoUpload extends ActionWithDestination[Batch, BatchReply]{
  private var path: String = null
  def setPath(path: String) {
    this.path = normalizeBasePath(path)
  }

  private def normalizeBasePath(path: String): String = {
    var basePath: String = if ((path == null)) "/" else path
    if (!basePath.endsWith("/")) {
      basePath = basePath + "/"
    }
    if (!basePath.startsWith("/")) {
      basePath = "/" + basePath
    }
    return basePath
  }

  protected def executeImpl(batch: Batch) {

    val aclBatch: util.List[ACL] = new util.ArrayList[ACL]

    for (fileInfo <- batch) {
      val acl: Map[String, Map[String, String]] = fileInfo.get(Parameters.PARAM_ACL).asInstanceOf[Map[String, Map[String, String]]]
      if (acl != null) {
        val normalizedAcl: util.Map[String, util.Map[String, String]] = new util.HashMap[String, util.Map[String, String]]
        for (aclPath <- acl.keySet) {
          normalizedAcl.put(normalizeAclPath(path, aclPath), acl.get(aclPath).get)
        }
        val inheritPermissions: Boolean = getInheritPermissionsFromParameterMap(fileInfo)
        aclBatch.add(new ACL(normalizedAcl, inheritPermissions))
      }
    }

    upload(batch, aclBatch)

  }

  private var writeOption: WriteOption = null

  def setWriteOption(writeOption: String) {
    this.writeOption = WriteOption.valueOf(writeOption)
  }

  private def upload(batch: Batch, acls: util.List[ACL]) {
    val documentsToUpload: util.List[Document] = new util.ArrayList[Document]
    val documentFileInfoMapping: java.util.Map[Document, FileInfo] = new util.HashMap[Document, FileInfo]
    for (parameterMap <- batch) {
      val relativePath: String = getParameterWithDefault(parameterMap, Parameters.PARAM_RELATIVE_PATH, "")
      val remotePath: String = normalizeRemotePath(path, relativePath)
      val mimeType: String = getParameterWithDefault(parameterMap, Parameters.PARAM_MIMETYPE, "text/plain")
      val namespace: String = getParameterWithDefault(parameterMap, Parameters.PARAM_NAMESPACE, "{http://www.alfresco.org/model/content/1.0}")
      val contentType: String = getParameterWithDefault(parameterMap, Parameters.PARAM_CONTENTTYPE, "content")
      val description: String = getParameterWithDefault(parameterMap, Parameters.PARAM_DESCRIPTION, "")
      val metadata: java.util.Map[String, String] = parameterMap.get(Parameters.PARAM_METADATA).asInstanceOf[java.util.Map[String, String]]
      val multiValueMetadata: java.util.Map[String, String] = parameterMap.get(Parameters.PARAM_MULTI_VALUE_METADATA).asInstanceOf[java.util.Map[String, String]]
      val file: File = parameterMap.get(Parameters.PARAM_FILE).asInstanceOf[File]
      val contentUrl: String = parameterMap.get(Parameters.PARAM_CONTENTURL).asInstanceOf[String]
      val document: Document = new Document(file, mimeType, remotePath, description, namespace, contentType, metadata, multiValueMetadata, contentUrl)
      documentsToUpload.add(document)
      documentFileInfoMapping.put(document, parameterMap)
    }
    sendTaskToDestination(batch, new SendBatchMessage(writeOption, documentsToUpload), reply => {
        sendingContext.sendMessage(new BatchACLMessage(reply.uploadResults, batch, acls))
    })
  }

  private def normalizeRemotePath(basePath: String, relativePathInput: String): String = {
    var relativePath: String = relativePathInput.replace("\\", "/")
    if (relativePath.startsWith("/")) {
      relativePath = relativePath.substring(1)
    }
    var remotePath: String = basePath + relativePath
    val components = remotePath.split("/")
    remotePath = ""
    for(component <- components){
        if ("" == component) {
          remotePath += "/"
        }
        else if (component.contains(":")) {
          remotePath += component + "/"
        }
        else {
          remotePath += "cm:" + component + "/"
        }
      }
    if (remotePath.length > 0) {
      remotePath = remotePath.substring(0, remotePath.length - 1)
    }
    return remotePath
  }

  private def getParameterWithDefault(parameterMap: util.Map[String, AnyRef], parameter: String, defaultValue: String): String = {
    var value: String = parameterMap.get(parameter).asInstanceOf[String]
    value = if ((value != null)) value else defaultValue
    return value
  }

  private def normalizeAclPath(basePath: String, aclPath: String): String = {
    var parserAclPath: String = aclPath
    if (parserAclPath.startsWith("/")) {
      parserAclPath = parserAclPath.substring(1, parserAclPath.length)
    }
    if (parserAclPath.endsWith("/")) {
      parserAclPath = parserAclPath.substring(0, parserAclPath.length - 1)
    }
    var remoteACLPath: String = basePath + parserAclPath
    val aclComponents = remoteACLPath.split("/")
    remoteACLPath = ""
    for (aclComponent <- aclComponents) {
      if ("" == aclComponent) {
        remoteACLPath += "/"
      }
      else if (aclComponent.contains(":")) {
        remoteACLPath += aclComponent + "/"
      }
      else {
        remoteACLPath += "cm:" + aclComponent + "/"
      }
    }
    remoteACLPath = remoteACLPath.substring(0, remoteACLPath.length - 1)
    logger.debug("ACL path: " + remoteACLPath)
    return remoteACLPath
  }

  private def getInheritPermissionsFromParameterMap(parameterMap: util.Map[String, AnyRef]): Boolean = {
    var inheritPermissions = false
    if (parameterMap.get(Parameters.PARAM_INHERIT_PERMISSIONS) == null) {
      inheritPermissions = false
    }
    else {
      inheritPermissions = parameterMap.get(Parameters.PARAM_INHERIT_PERMISSIONS).asInstanceOf[Boolean]
    }
    return inheritPermissions
  }

  /**
   *
   * @param message The message for which the error reply occurred
   * @param reply The Exception that happened in the reply
   */
  override def handleErrorReply(original: Batch, message: AnyRef, reply: Exception) {
    message match {
      case m: SendBatchMessage => {
          original.foreach {
            fileInfo => {
              handleError(fileInfo, reply)
            }
        }
      }
      case _ => super.handleErrorReply(original, message, reply)
    }
  }
}

object AlfrescoUpload {
  final val PARAM_PATH: String = "path"
  final val PARAM_WRITEOPTION: String = "writeOption"
}
