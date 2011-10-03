package eu.xenit.move2alf.core.action;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public class EmptyMetadataAction extends Action {

	private static final Logger logger = LoggerFactory
			.getLogger(EmptyMetadataAction.class);

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		// write to metadata map
		Map<String, String> metadata = (Map<String, String>) parameterMap
				.get(Parameters.PARAM_METADATA);
		if (metadata == null) {
			metadata = new HashMap<String, String>();
			parameterMap.put(Parameters.PARAM_METADATA, metadata);
		}

		parameterMap.put(Parameters.PARAM_NAMESPACE,
				"{http://www.alfresco.org/model/content/1.0}");
		parameterMap.put(Parameters.PARAM_CONTENTTYPE, "content");
	}

	@Override
	public String getCategory() {
		return ConfigurableObject.CAT_METADATA;
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
