package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.action.metadata.FilesystemMetadataLoader;

public class FilesystemMetadataAction extends MetadataAction {
	
	protected void initMetadataLoaders() {
		metadataLoaders.add(new FilesystemMetadataLoader());
	}
}
