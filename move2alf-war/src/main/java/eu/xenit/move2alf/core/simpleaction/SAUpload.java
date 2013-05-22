package eu.xenit.move2alf.core.simpleaction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.core.ApplicationContextProvider;
import eu.xenit.move2alf.core.action.ActionInfo;
import eu.xenit.move2alf.pipeline.actions.EOCAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.simpleaction.data.Batch;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.simpleaction.helpers.SimpleActionWithSourceSink;
import eu.xenit.move2alf.core.sourcesink.ACL;
import eu.xenit.move2alf.core.sourcesink.WriteOption;
import eu.xenit.move2alf.logic.usageservice.UsageService;
import eu.xenit.move2alf.repository.UploadResult;
import eu.xenit.move2alf.repository.alfresco.ws.Document;

@ActionInfo(classId = "SAUpload",
            description = "Uploads files to the configured sourcesink")
public class SAUpload extends SimpleActionWithSourceSink<FileInfo> implements EOCAware{

	private static final Logger logger = LoggerFactory
			.getLogger(SAUpload.class);


	private UsageService usageService = (UsageService) ApplicationContextProvider.getApplicationContext().getBean("usageService");


	private List<FileInfo> uploadAndSetACLs(final Batch batch, final List<ACL> acls) {
		boolean batchFailed = false;
		String errorMessage = "";
		List<UploadResult> results = null;
		try {
			results = upload(batch);

			for (final ACL acl : acls) {
				getSink().setACL(getSinkConfig(), acl);
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

	private List<UploadResult> upload(final Batch batch) {
		final List<Document> documentsToUpload = new ArrayList<Document>();
		final String basePath = normalizeBasePath(path);

		Map<Document, FileInfo> documentFileInfoMapping = new HashMap<Document, FileInfo>();
		for (final FileInfo parameterMap : batch) {
			final String relativePath = getParameterWithDefault(parameterMap,
					Parameters.PARAM_RELATIVE_PATH, "");
			final String remotePath = normalizeRemotePath(basePath,
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

			final Document document = new Document(file, mimeType, remotePath,
					description, namespace, contentType, metadata,
					multiValueMetadata);
			documentsToUpload.add(document);
			documentFileInfoMapping.put(document, parameterMap);
		}

		return getSink().sendBatch(getSinkConfig(), writeOption,
				documentsToUpload);
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

    public static final String PARAM_BATCH_SIZE = "batchSize";
    private int batchSize;
    public void setBatchSize(String batchSize){
        this.batchSize = Integer.parseInt(batchSize);
    }

    public static final String PARAM_PATH = "path";
    private String path;
    public void setPath(String path){
        this.path = path;
    }

    Batch batch = new Batch();
    List<ACL> aclBatch = new ArrayList<ACL>();

    @Override
    public void executeImpl(FileInfo fileInfo) {
        if (usageService.isBlockedByDocumentCounter()) {
            throw new Move2AlfException("Document counter is 0.");
        }

        logger.debug("Queueing file for upload: " + ((File) fileInfo.get(Parameters.PARAM_FILE)).getName());
        batch.add(fileInfo);

        final Map<String, Map<String, String>> acl = (Map<String, Map<String, String>>) fileInfo
                .get(Parameters.PARAM_ACL);
        if (acl != null) {
            final String basePath = normalizeBasePath(path);
            final Map<String, Map<String, String>> normalizedAcl = new HashMap<String, Map<String, String>>();
            for (final String aclPath : acl.keySet()) {
                normalizedAcl.put(normalizeAclPath(basePath, aclPath),
                        acl.get(aclPath));
            }
            final boolean inheritPermissions = getInheritPermissionsFromParameterMap(fileInfo);
            aclBatch.add(new ACL(normalizedAcl, inheritPermissions));
        }

        if(batch.size() == batchSize){
            logger.debug("Batch size reached, uploading " + batchSize + " files");
            uploadBatch();
        }
    }

    private void uploadBatch() {
        List<FileInfo> output = uploadAndSetACLs(batch, aclBatch);
        for(FileInfo fileInfo: output){
            sendMessage(fileInfo);
        }
        batch.clear();
        aclBatch.clear();
    }

    @Override
    public void beforeSendEOC() {
        logger.debug("EOC triggered upload.");
        uploadBatch();
    }
}
