package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.action.metadata.XmlPropertiesFileMetadataLoader;

public class XmlPropertiesFileMetadataAction extends MetadataAction {
	
	protected void initMetadataLoader() {
		metadataLoader = new XmlPropertiesFileMetadataLoader();
		//TODO meerdere loaders toelaten
		//bv optie toevoegen: + FilesystemMetadataLoader ofwel met checkboxen werken en meerdere keuzes toelaten
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