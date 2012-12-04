package eu.xenit.move2alf.repository.alfresco.ws;

import java.io.File;
import java.util.Map;

import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.CMLUpdate;
import org.alfresco.webservice.types.Reference;

public class Document {
	public final File file;
	public final String mimeType;
	public final String spacePath;
	public final String description;
	public final String contentModelNamespace;
	public final String contentModelType;
	public final Map<String, String> meta;
	public final Map<String, String> multiValueMeta;

	public Document(final File file, final String mimeType,
			final String spacePath, final String description,
			final String contentModelNamespace, final String contentModelType,
			final Map<String, String> meta,
			final Map<String, String> multiValueMeta) {
		this.file = file;
		this.mimeType = mimeType;
		this.spacePath = spacePath;
		this.description = description;
		this.contentModelNamespace = contentModelNamespace;
		this.contentModelType = contentModelType;
		this.meta = meta;
		this.multiValueMeta = multiValueMeta;
	}
	
	public CMLUpdate toCMLUpdate(Reference ref) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public CMLCreate toCMLCreate(Reference createSpaceIfNotExists) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getXpath() {
		// TODO Auto-generated method stub
		return null;
	}
}
