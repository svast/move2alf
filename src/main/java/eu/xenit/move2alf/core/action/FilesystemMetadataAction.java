package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.action.metadata.FilesystemMetadataLoader;

public class FilesystemMetadataAction extends MetadataAction {
	
	protected void initMetadataLoader() {
		metadataLoader = new FilesystemMetadataLoader();
	}

	@Override
	public String getDescription() {
		return "Read metadata from filesystem";
	}

	@Override
	public String getName() {
		return "Filesystem metadata";
	}
}
