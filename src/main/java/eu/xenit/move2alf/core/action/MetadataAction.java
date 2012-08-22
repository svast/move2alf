package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.action.metadata.MetadataLoader;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public abstract class MetadataAction extends Action {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MetadataAction.class);
	
	protected MetadataLoader metadataLoader;

	@Override
	protected final void executeImpl(ConfiguredAction configuredAction, Map<String, Object> parameterMap) {
		File file = (File) parameterMap.get(Parameters.PARAM_FILE);
		
		if ( metadataLoader.hasMetadata(file) ) {
			@SuppressWarnings("unchecked")
			Map<String, String> metadata = (Map<String, String>) parameterMap.get(Parameters.PARAM_METADATA);
			if (metadata == null) {
				metadata = new HashMap<String, String>();
				parameterMap.put(Parameters.PARAM_METADATA, metadata);
			}
			// TODO
			String dirname = (String) parameterMap.get(Parameters.PARAM_INPUT_PATH);
			Map<String, String> propertyMap = metadataLoader.loadMetadata(dirname, file);
			metadata.putAll(propertyMap);
		}

		//TODO moet dit nog in de "if", maw moet dit ook voor de .metadata.properties.xml file gezet worden?
		parameterMap.put(Parameters.PARAM_NAMESPACE, "{http://www.alfresco.org/model/content/1.0}");
		parameterMap.put(Parameters.PARAM_CONTENTTYPE, "content");
	}

	@Override
	public final String getCategory() {
		return ConfigurableObject.CAT_METADATA;
	}
}