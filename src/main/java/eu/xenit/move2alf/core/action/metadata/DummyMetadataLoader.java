package eu.xenit.move2alf.core.action.metadata;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class DummyMetadataLoader implements MetadataLoader {

	public boolean hasMetadata(File file) {
		return true;
	}

	public Map<String, String> loadMetadata(File file) {
		Map<String, String> metadata = new HashMap<String, String>();
		return metadata;
	}
}