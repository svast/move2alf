package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.action.metadata.DummyMetadataLoader;

@ActionInfo(classId = "EmptyMetadataAction",
            category = ConfigurableObject.CAT_METADATA,
            description = "No metadata is added.")
public class EmptyMetadataAction extends MetadataAction {
	
	protected void initMetadataLoaders() {
		metadataLoaders.add(new DummyMetadataLoader());
	}

}
