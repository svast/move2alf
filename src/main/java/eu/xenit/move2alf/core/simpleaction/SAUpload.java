package eu.xenit.move2alf.core.simpleaction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.Batch;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.simpleaction.helpers.SimpleActionWithSourceSink;
import eu.xenit.move2alf.core.sourcesink.ACL;
import eu.xenit.move2alf.core.sourcesink.SourceSink;
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

	/*
	 * Store batches here before submitting.
	 * 
	 * The map is necessary because multiple jobs could be running at the same
	 * time so threads can be reused over jobs. Cycle id is used as key.
	 * 
	 * ThreadLocal to prevent concurrency complexity, multiple threads can be
	 * uploading at the same time.
	 */
	private static final ThreadLocal<Map<Integer, Batch>> batches = new ThreadLocal<Map<Integer, Batch>>() {
		@Override
		protected synchronized Map<Integer, Batch> initialValue() {
			return new HashMap<Integer, Batch>();
		}
	};

	private static final ThreadLocal<Map<Integer, List<ACL>>> aclBatches = new ThreadLocal<Map<Integer, List<ACL>>>() {
		@Override
		protected synchronized Map<Integer, List<ACL>> initialValue() {
			return new HashMap<Integer, List<ACL>>();
		}
	};

	public SAUpload(final SourceSink sink, final ConfiguredSourceSink sinkConfig) {
		super(sink, sinkConfig);
	}

	@Override
	public List<FileInfo> execute(final FileInfo parameterMap,
			final ActionConfig config) {
		final Integer maxBatchSize = Integer.parseInt(config
				.get(PARAM_BATCH_SIZE));
		final Integer cycleId = (Integer) parameterMap
				.get(Parameters.PARAM_CYCLE);

		final Batch batch = getCurrentBatch(cycleId);
		final List<ACL> aclBatch = getCurrentACLBatch(cycleId);

		if (batch.size() < maxBatchSize) {
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
			final List<FileInfo> output = upload(batch, config);
			batch.clear();

			for (final ACL acl : aclBatch) {
				getSink().setACL(getSinkConfig(), acl);
			}
			aclBatch.clear();
			return output;
		} else {
			return new ArrayList<FileInfo>();
		}
	}

	private List<ACL> getCurrentACLBatch(final Integer cycleId) {
		final Map<Integer, List<ACL>> aclBatches = SAUpload.aclBatches.get();
		List<ACL> aclBatch = null;
		if (aclBatches.containsKey(cycleId)) {
			aclBatch = aclBatches.get(cycleId);
		} else {
			aclBatch = new ArrayList<ACL>();
			aclBatches.put(cycleId, aclBatch);
		}
		return aclBatch;
	}

	private Batch getCurrentBatch(final Integer cycleId) {
		final Map<Integer, Batch> batches = SAUpload.batches.get();
		Batch batch = null;
		if (batches.containsKey(cycleId)) {
			batch = batches.get(cycleId);
		} else {
			batch = new Batch();
			batches.put(cycleId, batch);
		}
		return batch;
	}

	private List<FileInfo> upload(final Batch batch, final ActionConfig config) {
		final List<FileInfo> output = new ArrayList<FileInfo>();
		final List<Document> documentsToUpload = new ArrayList<Document>();
		final String basePath = normalizeBasePath(config.get(PARAM_PATH));
		for (final FileInfo parameterMap : batch) {
			final FileInfo newParameterMap = new FileInfo();
			newParameterMap.putAll(parameterMap);

			final String relativePath = getParameterWithDefault(parameterMap,
					Parameters.PARAM_RELATIVE_PATH, "");
			final String remotePath = normalizeRemotePath(basePath,
					relativePath);
			logger.debug("Writing to " + remotePath);
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

			output.add(newParameterMap);
		}
		getSink().sendBatch(getSinkConfig(), config.get(PARAM_DOCUMENT_EXISTS),
				documentsToUpload);
		return output;
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
}
