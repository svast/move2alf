package eu.xenit.move2alf.core.action.metadata;

import java.io.File;
import java.util.Map;

public interface MetadataLoader {
	
	public boolean hasMetadata (File file);

	public Map<String, String> loadMetadata (File file);

}
