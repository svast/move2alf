package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//import org.alfresco.util.ISO8601DateFormat;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public class FilesystemMetadataAction extends Action {

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		File file = (File) parameterMap.get(Parameters.PARAM_FILE);
		long lastModified = file.lastModified();
		
		// write to metadata map
		Map<String, String> metadata = (Map<String, String>) parameterMap.get(Parameters.PARAM_METADATA);
		if (metadata == null) {
			metadata = new HashMap<String, String>();
			parameterMap.put(Parameters.PARAM_METADATA, metadata);
		}
//		metadata.put("modified", ISO8601DateFormat.format(dateLastModified));
		
		parameterMap.put(Parameters.PARAM_NAMESPACE, "{http://www.alfresco.org/model/content/1.0}");
		parameterMap.put(Parameters.PARAM_CONTENTTYPE, "content");
	}

	@Override
	public String getCategory() {
		return ConfigurableObject.CAT_METADATA;
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
