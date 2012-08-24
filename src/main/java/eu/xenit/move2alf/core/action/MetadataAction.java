package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

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
	
	protected Vector<MetadataLoader> metadataLoaders;

	public MetadataAction() {
		super();
		metadataLoaders = new Vector<MetadataLoader>();
		initMetadataLoaders();
	}
	
	protected abstract void initMetadataLoaders();

	@Override
	protected final void executeImpl(ConfiguredAction configuredAction, Map<String, Object> parameterMap) {
		File file = (File) parameterMap.get(Parameters.PARAM_FILE);

		@SuppressWarnings("unchecked")
		Map<String, String> metadata = (Map<String, String>) parameterMap.get(Parameters.PARAM_METADATA);
		for (MetadataLoader metadataLoader : metadataLoaders) {
			if ( metadataLoader.hasMetadata(file) ) {
				if (metadata == null) {
					metadata = new HashMap<String, String>();
					parameterMap.put(Parameters.PARAM_METADATA, metadata);
				}
				Map<String, String> propertyMap = metadataLoader.loadMetadata(file);
				metadata.putAll(propertyMap);
				
				parameterMap.put(Parameters.PARAM_NAMESPACE, "{http://www.alfresco.org/model/content/1.0}");
				parameterMap.put(Parameters.PARAM_CONTENTTYPE, "content");
			}
		}
	}

	@Override
	public final String getCategory() {
		return ConfigurableObject.CAT_METADATA;
	}
}