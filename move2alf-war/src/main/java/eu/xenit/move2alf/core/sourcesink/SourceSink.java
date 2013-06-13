package eu.xenit.move2alf.core.sourcesink;

import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.repository.IllegalDocumentException;
import eu.xenit.move2alf.repository.UploadResult;
import eu.xenit.move2alf.repository.alfresco.ws.Document;

import java.io.File;
import java.util.List;
import java.util.Map;

public abstract class SourceSink extends ConfigurableObject {

    public int getId() {
        return id;
    }

    private int id;

    public void setId(int id){
        this.id = id;
    }

	public abstract void send(
			WriteOption docExistsMode, String remotePath,
			String mimeType, String namespace, String contentType,
			String description, Map<String, String> metadata,
			Map<String, String> multiValueMetadata,
			File document) throws IllegalDocumentException;

	public abstract List<UploadResult> sendBatch(
			WriteOption docExistsMode, List<Document> documents);

	public abstract void setACL(ACL acls);

	public abstract List<File> list(String path, boolean recursive);

	public abstract boolean exists(	String remotePath, String name);

	public abstract void delete(String remotePath, String name, DeleteOption option);

	public abstract void clearCaches();

	public abstract boolean fileNameExists(String name);

    public abstract String putContent(File file, String mimeType);
}
