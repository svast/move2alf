package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.action.metadata.FilesystemMetadataLoader;

@ActionInfo(classId = "FilesystemMetadataAction",
            category = ConfigurableObject.CAT_METADATA,
            description = "Action that gets metadata from the filesystem")
public class FilesystemMetadataAction extends MetadataAction {
	
	protected void initMetadataLoaders() {
		metadataLoaders.add(new FilesystemMetadataLoader());
	}
}
