package eu.xenit.move2alf.core.sourcesink;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.repository.IllegalDocumentException;
import eu.xenit.move2alf.repository.RepositoryAccessException;
import eu.xenit.move2alf.repository.UploadResult;
import eu.xenit.move2alf.repository.alfresco.ws.Document;

public abstract class SourceSink extends ConfigurableObject {

    public int getId() {
        return id;
    }

    private int id;

    public void setId(int id){
        this.id = id;
    }

	public abstract void send(ConfiguredSourceSink configuredSourceSink,
			WriteOption docExistsMode, String remotePath,
			String mimeType, String namespace, String contentType,
			String description, Map<String, String> metadata,
			Map<String, String> multiValueMetadata,
			File document) throws IllegalDocumentException;

	public abstract List<UploadResult> sendBatch(ConfiguredSourceSink configuredSourceSink,
			WriteOption docExistsMode, List<Document> documents);

	public abstract void setACL(ConfiguredSourceSink configuredSourceSink,
			ACL acls);

	public abstract List<File> list(ConfiguredSourceSink sourceConfig,
			String path, boolean recursive);

	public abstract boolean exists(ConfiguredSourceSink sinkConfig,
			String remotePath, String name);

	public abstract void delete(ConfiguredSourceSink sinkConfig,
			String remotePath, String name, DeleteOption option);

	public abstract void clearCaches(ConfiguredSourceSink sinkConfig);

	public abstract boolean fileNameExists(ConfiguredSourceSink sinkConfig, String name);

    public abstract String putContent(ConfiguredSourceSink sinkConfig, File file, String mimeType);
}
