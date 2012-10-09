package eu.xenit.move2alf.core.sourcesink;

import java.io.File;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.repository.alfresco.ws.Document;

public abstract class SourceSink extends ConfigurableObject {
	public static final String MODE_SKIP = "Skip";
	public static final String MODE_SKIP_AND_LOG = "SkipAndLog";
	public static final String MODE_OVERWRITE = "Overwrite";

	public abstract void send(ConfiguredSourceSink configuredSourceSink,
			String docExistsMode, String basePath, String remotePath,
			String mimeType, String namespace, String contentType,
			String description, Map<String, String> metadata,
			Map<String, String> multiValueMetadata,
			Map<String, Map<String, String>> acl, boolean inheritPermissions,
			File document);

	public abstract void sendBatch(ConfiguredSourceSink configuredSourceSink,
			String docExistsMode, String basePath, List<Document> documents,
			List<ACL> acls);

	public abstract List<File> list(ConfiguredSourceSink sourceConfig,
			String path, boolean recursive);

	public abstract boolean exists(ConfiguredSourceSink sinkConfig,
			String remotePath, String name);

	public abstract void delete(ConfiguredSourceSink sinkConfig,
			String remotePath, String name);
}
