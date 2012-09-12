package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.action.metadata.DummyMetadataLoader;


public class EmptyMetadataAction extends MetadataAction {
	
	protected void initMetadataLoaders() {
		metadataLoaders.add(new DummyMetadataLoader());
	}

	@Override
	public String getDescription() {
		return "Do not set metadata";
	}

	@Override
	public String getName() {
		return "Empty metadata";
	}
}
