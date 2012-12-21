package eu.xenit.move2alf.core.sourcesink;

import java.io.File;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.repository.IllegalDocumentException;
import eu.xenit.move2alf.repository.PartialUploadFailureException;
import eu.xenit.move2alf.repository.alfresco.ws.Document;

public abstract class SourceSink extends ConfigurableObject {
	public static final String MODE_SKIP = "Skip";
	public static final String MODE_SKIP_AND_LOG = "SkipAndLog";
	public static final String MODE_OVERWRITE = "Overwrite";

	public abstract void send(ConfiguredSourceSink configuredSourceSink,
			String docExistsMode, String remotePath,
			String mimeType, String namespace, String contentType,
			String description, Map<String, String> metadata,
			Map<String, String> multiValueMetadata,
			File document) throws IllegalDocumentException;

	public abstract void sendBatch(ConfiguredSourceSink configuredSourceSink,
			String docExistsMode, List<Document> documents) throws PartialUploadFailureException;

	public abstract void setACL(ConfiguredSourceSink configuredSourceSink,
			ACL acls);

	public abstract List<File> list(ConfiguredSourceSink sourceConfig,
			String path, boolean recursive);

	public abstract boolean exists(ConfiguredSourceSink sinkConfig,
			String remotePath, String name);

	public abstract void delete(ConfiguredSourceSink sinkConfig,
			String remotePath, String name);

	public abstract void clearCaches(ConfiguredSourceSink sinkConfig);

	public abstract boolean fileNameExists(ConfiguredSourceSink sinkConfig, String name);
}
