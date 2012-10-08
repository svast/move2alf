package eu.xenit.move2alf.core.simpleaction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.SourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.Batch;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.simpleaction.helpers.SimpleActionWithSourceSink;

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

	public SAUpload(final SourceSink sink, final ConfiguredSourceSink sinkConfig) {
		super(sink, sinkConfig);
	}

	@Override
	public List<FileInfo> execute(final FileInfo parameterMap,
			final ActionConfig config) {

		final Map<Integer, Batch> batches = SAUpload.batches.get();
		Batch batch = null;
		final Integer cycleId = (Integer) parameterMap
				.get(Parameters.PARAM_CYCLE);
		if (batches.containsKey(cycleId)) {
			batch = batches.get(cycleId);
		} else {
			batch = new Batch();
			batches.put(cycleId, batch);
		}
		final Integer maxBatchSize = Integer.parseInt(config
				.get(PARAM_BATCH_SIZE));

		if (batch.size() < maxBatchSize) {
			batch.add(parameterMap);
		}

		if (batch.size() == maxBatchSize) {
			final List<FileInfo> output = upload(batch, config);
			batch.clear();
			return output;
		} else {
			return new ArrayList<FileInfo>();
		}
	}

	private List<FileInfo> upload(final Batch batch, final ActionConfig config) {
		final List<FileInfo> output = new ArrayList<FileInfo>();
		for (final FileInfo parameterMap : batch) {
			final FileInfo newParameterMap = new FileInfo();
			newParameterMap.putAll(parameterMap);

			final String basePath = normalizeBasePath(config.get(PARAM_PATH));
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
			final Map<String, Map<String, String>> acl = (Map<String, Map<String, String>>) parameterMap
					.get(Parameters.PARAM_ACL);
			final boolean inheritPermissions = getInheritPermissionsFromParameterMap(parameterMap);
			final File document = (File) parameterMap
					.get(Parameters.PARAM_FILE);

			getSink().send(getSinkConfig(), config.get(PARAM_DOCUMENT_EXISTS),
					basePath, remotePath, mimeType, namespace, contentType,
					description, metadata, multiValueMetadata, acl,
					inheritPermissions, document);
			output.add(newParameterMap);
		}
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

	private static String getParameterWithDefault(
			final Map<String, Object> parameterMap, final String parameter,
			final String defaultValue) {
		String value = (String) parameterMap.get(parameter);
		value = (value != null) ? value : defaultValue;
		return value;
	}
}
