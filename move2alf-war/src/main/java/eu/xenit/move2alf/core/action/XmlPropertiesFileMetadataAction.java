package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.action.metadata.XmlPropertiesFileMetadataLoader;

public class XmlPropertiesFileMetadataAction extends MetadataAction {
	
	protected void initMetadataLoaders() {
		metadataLoaders.add(new XmlPropertiesFileMetadataLoader());
		//TODO meerdere loaders toelaten
		//metadataLoaders.add(new FilesystemMetadataLoader());
	}

	@Override
	public String getDescription() {
		return "Read metadata from xml properties file";
	}

	@Override
	public String getName() {
		return "Xml properties file metadata";
	}	
}