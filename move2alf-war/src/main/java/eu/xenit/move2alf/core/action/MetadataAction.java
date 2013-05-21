package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.*;

import eu.xenit.move2alf.core.action.messages.FileInfoMessage;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.action.metadata.MetadataLoader;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public abstract class MetadataAction extends Move2AlfReceivingAction<FileInfoMessage> {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MetadataAction.class);
	
	protected List<MetadataLoader> metadataLoaders;

	public MetadataAction() {
		super();
		metadataLoaders = new ArrayList<MetadataLoader>();
		initMetadataLoaders();
	}
	
	protected abstract void initMetadataLoaders();

	@Override
	protected void executeImpl(FileInfoMessage message) {
        FileInfo fileInfo = message.fileInfo;
		File file = (File) fileInfo.get(Parameters.PARAM_FILE);

		@SuppressWarnings("unchecked")
		Map<String, String> metadata = (Map<String, String>) fileInfo.get(Parameters.PARAM_METADATA);
		for (MetadataLoader metadataLoader : metadataLoaders) {
			if ( metadataLoader.hasMetadata(file) ) {
				if (metadata == null) {
					metadata = new HashMap<String, String>();
					fileInfo.put(Parameters.PARAM_METADATA, metadata);
				}
				Map<String, String> propertyMap = metadataLoader.loadMetadata(file);
				metadata.putAll(propertyMap);

				fileInfo.put(Parameters.PARAM_NAMESPACE, "{http://www.alfresco.org/model/content/1.0}");
				fileInfo.put(Parameters.PARAM_CONTENTTYPE, "content");
			}
		}
        sendMessage(new FileInfoMessage(fileInfo));
	}
}