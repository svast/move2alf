package eu.xenit.move2alf.core.simpleaction;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.Batch;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.simpleaction.helpers.SimpleActionWithSourceSink;
import eu.xenit.move2alf.core.sourcesink.ACL;
import eu.xenit.move2alf.core.sourcesink.SourceSink;
import eu.xenit.move2alf.logic.usageservice.UsageService;
import eu.xenit.move2alf.repository.alfresco.ws.Document;

public class SAUpload extends SimpleActionWithSourceSink {

	private static final Logger logger = LoggerFactory
			.getLogger(SAUpload.class);

	public static final String PARAM_PATH = "path";
	/**
	 * SourceSink.MODE_SKIP or SourceSink.MODE_SKIP_AND_LOG or
	 * SourceSink.MODE_OVERWRITE
	 */
	public static final String PARAM_DOCUMENT_EXISTS = "documentExists";
	public static final String PARAM_BATCH_SIZE = "batchSize";
	public static final String STATE_BATCH = "batch";
	public static final String STATE_ACL_BATCH = "aclBatch";

	private UsageService usageService;


	public SAUpload(final SourceSink sink, final ConfiguredSourceSink sinkConfig, UsageService usageService) {
		super(sink, sinkConfig);
		this.usageService = usageService;
	}

	@Override
	public List<FileInfo> execute(final FileInfo parameterMap,
								  final ActionConfig config, final Map<String, Serializable> state) {
		//TODO jonas - adapt to new batch processing
		if ( usageService.isBlockedByDocumentCounter() ) {
			throw new Move2AlfException("Document counter is 0.");
		}
		
		final Integer maxBatchSize = Integer.parseInt(config
				.get(PARAM_BATCH_SIZE));

		final Batch batch = getCurrentBatch(state);
		final List<ACL> aclBatch = getCurrentACLBatch(state);

		boolean uploadNow = false;
		Batch batchToUpload = new Batch();
		List<ACL> aclsToSet = new ArrayList<ACL>();

		synchronized (batch) {
			if (batch.size() < maxBatchSize) {
				logger.debug("Queueing file for upload: " + ((File) parameterMap.get(Parameters.PARAM_FILE)).getName());
				batch.add(parameterMap);

				final Map<String, Map<String, String>> acl = (Map<String, Map<String, String>>) parameterMap
						.get(Parameters.PARAM_ACL);
				if (acl != null) {
					final String basePath = normalizeBasePath(config
							.get(PARAM_PATH));
					final Map<String, Map<String, String>> normalizedAcl = new HashMap<String, Map<String, String>>();
					for (final String aclPath : acl.keySet()) {
						normalizedAcl.put(normalizeAclPath(basePath, aclPath),
								acl.get(aclPath));
					}
					final boolean inheritPermissions = getInheritPermissionsFromParameterMap(parameterMap);
					aclBatch.add(new ACL(normalizedAcl, inheritPermissions));
				}
			} else {
				// this should never happen
				throw new Move2AlfException(
						"Batch size too big, failed to commit batch?");
			}

			if (batch.size() == maxBatchSize) {
				logger.debug("Batch size reached, uploading " + maxBatchSize + " files");
				uploadNow = true;
				// make a local copies
				batchToUpload = new Batch(batch);
				batch.clear();
				aclsToSet = new ArrayList<ACL>(aclBatch);
				aclBatch.clear();
			}
		}

		// Move slow IO code outside of synchronized block
		if (uploadNow) {
			final List<FileInfo> output = uploadAndSetACLs(config, batchToUpload, aclsToSet);
			return output;
		} else {
			return new ArrayList<FileInfo>();
		}
	}

	private List<FileInfo> uploadAndSetACLs(final ActionConfig config, final Batch batch, final List<ACL> acls) {
		boolean batchFailed = false;
		String errorMessage = "";
		try {
			upload(batch, config);
			for (final ACL acl : acls) {
				getSink().setACL(getSinkConfig(), acl);
			}
		} catch (final Exception e) {
			batchFailed = true;
			errorMessage = Util.getFullErrorMessage(e);
		}

		final List<FileInfo> output = new ArrayList<FileInfo>();
		for (final FileInfo oldParameterMap : batch) {
			final FileInfo newParameterMap = new FileInfo();
			newParameterMap.putAll(oldParameterMap);
			if (batchFailed) {
				newParameterMap.put(Parameters.PARAM_STATUS, Parameters.VALUE_FAILED);
				newParameterMap.put(Parameters.PARAM_ERROR_MESSAGE, errorMessage);
			}
			output.add(newParameterMap);
		}
		return output;
	}

	@Override
	public List<FileInfo> initializeState(final ActionConfig config, final Map<String, Serializable> state) {
		logger.debug("Initializing state");
		state.put(STATE_BATCH, new Batch());
		state.put(STATE_ACL_BATCH, new ArrayList<ACL>());

		getSink().clearCaches(getSinkConfig());

		return null;
	}

	@Override
	public List<FileInfo> cleanupState(final ActionConfig config, final Map<String, Serializable> state) {
		logger.debug("Cleaning up state");
		final Batch batch = getCurrentBatch(state);
		List<FileInfo> output = null;
		if (batch.size() > 0) {
			logger.debug("There are documents queued for upload, uploading " + batch.size() + " documents");
			final List<ACL> aclBatch = getCurrentACLBatch(state);
			output = uploadAndSetACLs(config, batch, aclBatch);
		} else {
			logger.debug("No documents queued");
		}
		state.remove(STATE_BATCH);
		state.remove(STATE_ACL_BATCH);
		return output;
	}

	private Batch getCurrentBatch(final Map<String, Serializable> state) {
		return (Batch) state.get(STATE_BATCH);
	}

	private List<ACL> getCurrentACLBatch(final Map<String, Serializable> state) {
		return (List<ACL>) state.get(STATE_ACL_BATCH);
	}

	private void upload(final Batch batch,
						final ActionConfig config) {
		final List<Document> documentsToUpload = new ArrayList<Document>();
		final String basePath = normalizeBasePath(config.get(PARAM_PATH));
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
		}
		getSink().sendBatch(getSinkConfig(), config.get(PARAM_DOCUMENT_EXISTS),
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
		remotePath = remotePath.substring(0, remotePath.length() - 1);
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

	@Override
	public String getDescription() {
		return "Uploading documents to Alfresco";
	}
}
