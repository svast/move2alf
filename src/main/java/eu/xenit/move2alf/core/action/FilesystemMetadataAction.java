package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.Date;
import java.util.Map;

import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public class FilesystemMetadataAction extends Action {

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		File file = (File) parameterMap.get("file");
		long lastModified = file.lastModified();
		// TODO: write to metadata map
		
		parameterMap.put("namespace", "{http://www.alfresco.org/model/content/1.0}");
		parameterMap.put("contenttype", "content");
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
