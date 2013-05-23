package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.action.metadata.XmlPropertiesFileMetadataLoader;

@ActionInfo(classId = "XmlPropertiesFileMetadataAction",
            category = ConfigurableObject.CAT_METADATA,
            description = "Can add metadata in bulk upload tool format")
public class XmlPropertiesFileMetadataAction extends MetadataAction {
	
	protected void initMetadataLoaders() {
		metadataLoaders.add(new XmlPropertiesFileMetadataLoader());
	}
}