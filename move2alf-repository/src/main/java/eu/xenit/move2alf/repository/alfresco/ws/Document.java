package eu.xenit.move2alf.repository.alfresco.ws;

import java.io.File;
import java.util.Map;

public class Document {
	public final File file;
    public final String name;
	public final String mimeType;
	public final String spacePath;
	public final String description;
	public final String contentModelNamespace;
	public final String contentModelType;
	public final Map<String, String> meta;
	public final Map<String, String> multiValueMeta;
    public final String contentUrl;

    public Document(final File file, final String name, final String mimeType,
                    final String spacePath, final String description,
                    final String contentModelNamespace, final String contentModelType,
                    final Map<String, String> meta,
                    final Map<String, String> multiValueMeta, final String contentUrl) {
        this.file = file;
        this.name = name;
        this.mimeType = mimeType;
        this.spacePath = spacePath;
        this.description = description;
        this.contentModelNamespace = contentModelNamespace;
        this.contentModelType = contentModelType;
        this.meta = meta;
        this.multiValueMeta = multiValueMeta;
        this.contentUrl = contentUrl;
    }
}
