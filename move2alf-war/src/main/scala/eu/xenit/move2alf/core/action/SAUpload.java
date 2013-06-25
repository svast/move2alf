package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.action.ActionWithDestination;
import eu.xenit.move2alf.core.action.messages.SetAclMessage;
import eu.xenit.move2alf.core.simpleaction.data.Batch;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.sourcesink.ACL;
import eu.xenit.move2alf.core.sourcesink.WriteOption;
import eu.xenit.move2alf.logic.usageservice.UsageService;
import eu.xenit.move2alf.repository.UploadResult;
import eu.xenit.move2alf.repository.alfresco.ws.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ClassInfo(classId = "SAUpload",
            description = "Uploads files to the configured sourcesink")
public class SAUpload extends ActionWithDestination<Batch> {

	private static final Logger logger = LoggerFactory
			.getLogger(SAUpload.class);

    @Autowired
    private UsageService usageService;


	private List<FileInfo> uploadAndSetACLs(final Batch batch, final List<ACL> acls) {
		boolean batchFailed = false;
		String errorMessage = "";
		List<UploadResult> results = null;
		try {
			results = upload(batch);

			for (final ACL acl : acls) {
                sendTaskToDestination(new SetAclMessage(acl));
			}
		} catch (final Exception e) {
			batchFailed = true;
			errorMessage = Util.getFullErrorMessage(e);
			logger.debug("Error message=" + errorMessage + " extracted from e=" + e);
		}

		final List<FileInfo> output = new ArrayList<FileInfo>();

		if (batchFailed) {
			for (final FileInfo oldParameterMap : batch) {
				final FileInfo newParameterMap = new FileInfo();
				newParameterMap.putAll(oldParameterMap);
				newParameterMap.put(Parameters.PARAM_STATUS, Parameters.VALUE_FAILED);
				newParameterMap.put(Parameters.PARAM_ERROR_MESSAGE, errorMessage);
				output.add(newParameterMap);
			}
		} else {
			final String inputPath = (batch.size() > 0) ? (String) batch.get(0).get(Parameters.PARAM_INPUT_PATH) : "";
			for (final UploadResult result : results) {
				final FileInfo newParameterMap = new FileInfo();
				newParameterMap.put(Parameters.PARAM_INPUT_PATH, inputPath);
				newParameterMap.put(Parameters.PARAM_FILE, result.getDocument().file);
				newParameterMap.put(Parameters.PARAM_STATUS, (result.getStatus() == UploadResult.VALUE_OK) ?
						Parameters.VALUE_OK : Parameters.VALUE_FAILED);
				newParameterMap.put(Parameters.PARAM_ERROR_MESSAGE, result.getMessage());
				newParameterMap.put(Parameters.PARAM_REFERENCE, result.getReference());
				output.add(newParameterMap);
			}
		}

		return output;
	}

    public static final String PARAM_WRITEOPTION = "writeOption";
    private WriteOption writeOption;
    public void setWriteOption(String writeOption){
        this.writeOption = WriteOption.valueOf(writeOption);
    }

	private void upload(final Batch batch) {
		final List<Document> documentsToUpload = new ArrayList<Document>();

		Map<Document, FileInfo> documentFileInfoMapping = new HashMap<Document, FileInfo>();
		for (final FileInfo parameterMap : batch) {
			final String relativePath = getParameterWithDefault(parameterMap,
					Parameters.PARAM_RELATIVE_PATH, "");
			final String remotePath = normalizeRemotePath(path,
					relativePath);
			final String mimeType = getParameterWithDefault(parameterMap,
					Parameters.PARAM_MIMETYPE, "text/plain");
			final String namespace = getParameterWithDefault(parameterMap,
					Parameters.PARAM_NAMESPACE,
					"{http://www.alfresco.org/model/content/1.0}");
			final String contentType = getParameterWithDefault(parameterMap,
					Parameters.PARAM_CONTENTTYPE, "content");
			final String description = getParameterWithDefault(parameterMap,
					Parameters.PARAM_DESCRIPTION, "");
			final Map<String, String> metadata = (Map<String, String>) parameterMap
					.get(Parameters.PARAM_METADATA);
			final Map<String, String> multiValueMetadata = (Map<String, String>) parameterMap
					.get(Parameters.PARAM_MULTI_VALUE_METADATA);

			final File file = (File) parameterMap.get(Parameters.PARAM_FILE);
            final String contentUrl = (String) parameterMap.get(Parameters.PARAM_CONTENTURL);

			final Document document = new Document(file, mimeType, remotePath,
					description, namespace, contentType, metadata,
					multiValueMetadata, contentUrl);
			documentsToUpload.add(document);
			documentFileInfoMapping.put(document, parameterMap);
		}
        sendTaskToDestination(SendBatchMessage(writeOption, documentsToUpload));
	}

	private static boolean getInheritPermissionsFromParameterMap(
			final Map<String, Object> parameterMap) {
		boolean inheritPermissions;
		if (parameterMap.get(Parameters.PARAM_INHERIT_PERMISSIONS) == null) {
			inheritPermissions = false;
		} else {
			inheritPermissions = (Boolean) parameterMap
					.get(Parameters.PARAM_INHERIT_PERMISSIONS);
		}
		return inheritPermissions;
	}

	// TODO: part (or all?) of this should move back to AlfrescoSourceSink: the
	// "cm:" prefix is Alfresco specific
	private static String normalizeRemotePath(final String basePath,
											  final String relativePathInput) {
		String relativePath = relativePathInput.replace("\\", "/");

		if (relativePath.startsWith("/")) {
			relativePath = relativePath.substring(1);
		}

		// add "cm:" in front of each path component
		String remotePath = basePath + relativePath;
		final String[] components = remotePath.split("/");
		remotePath = "";
		for (final String component : components) {
			if ("".equals(component)) {
				remotePath += "/";
			} else if (component.contains(":")) {
				remotePath += component + "/";
			} else {
				remotePath += "cm:" + component + "/";
			}
		}
		if (remotePath.length() > 0) {
			remotePath = remotePath.substring(0, remotePath.length() - 1);
		}
		return remotePath;
	}

	private static String normalizeBasePath(final String path) {
		String basePath = (path == null) ? "/" : path;
		if (!basePath.endsWith("/")) {
			basePath = basePath + "/";
		}

		if (!basePath.startsWith("/")) {
			basePath = "/" + basePath;
		}
		return basePath;
	}

	private String normalizeAclPath(final String basePath, final String aclPath) {
		String parserAclPath = aclPath;
		// add "cm:" in front of each path component
		if (parserAclPath.startsWith("/")) {
			parserAclPath = parserAclPath.substring(1, parserAclPath.length());
		}
		if (parserAclPath.endsWith("/")) {
			parserAclPath = parserAclPath.substring(0,
					parserAclPath.length() - 1);
		}

		String remoteACLPath = basePath + parserAclPath;
		final String[] aclComponents = remoteACLPath.split("/");
		remoteACLPath = "";
		for (final String aclComponent : aclComponents) {
			if ("".equals(aclComponent)) {
				remoteACLPath += "/";
			} else if (aclComponent.contains(":")) {
				remoteACLPath += aclComponent + "/";
			} else {
				remoteACLPath += "cm:" + aclComponent + "/";
			}
		}
		remoteACLPath = remoteACLPath.substring(0, remoteACLPath.length() - 1);

		logger.debug("ACL path: " + remoteACLPath);
		return remoteACLPath;
	}

	private static String getParameterWithDefault(
			final Map<String, Object> parameterMap, final String parameter,
			final String defaultValue) {
		String value = (String) parameterMap.get(parameter);
		value = (value != null) ? value : defaultValue;
		return value;
	}

    public static final String PARAM_PATH = "path";
    private String path;
    public void setPath(String path){
        this.path = normalizeBasePath(path);
    }

    @Override
    public void executeImpl(Batch batch) {
        if (usageService.isBlockedByDocumentCounter()) {
            throw new Move2AlfException("Document counter is 0.");
        }

        List<ACL> aclBatch = new ArrayList<ACL>();

        for(FileInfo fileInfo: batch){
            final Map<String, Map<String, String>> acl = (Map<String, Map<String, String>>) fileInfo
                    .get(Parameters.PARAM_ACL);
            if (acl != null) {
                final Map<String, Map<String, String>> normalizedAcl = new HashMap<String, Map<String, String>>();
                for (final String aclPath : acl.keySet()) {
                    normalizedAcl.put(normalizeAclPath(path, aclPath),
                            acl.get(aclPath));
                }
                final boolean inheritPermissions = getInheritPermissionsFromParameterMap(fileInfo);
                aclBatch.add(new ACL(normalizedAcl, inheritPermissions));
            }
        }

        for(FileInfo fileInfo: uploadAndSetACLs(batch, aclBatch)){
            sendMessage(fileInfo);
        }
    }
}
