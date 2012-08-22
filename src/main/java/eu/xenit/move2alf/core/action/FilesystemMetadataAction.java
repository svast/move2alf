package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.action.metadata.FilesystemMetadataLoader;

public class FilesystemMetadataAction extends MetadataAction {
	
	public FilesystemMetadataAction() {
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
