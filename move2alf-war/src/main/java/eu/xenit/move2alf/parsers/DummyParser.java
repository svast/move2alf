package eu.xenit.move2alf.parsers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.action.Action;
import eu.xenit.move2alf.core.dto.ConfiguredAction;


public class DummyParser extends Action {

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		parameterMap.put(Parameters.PARAM_NAMESPACE, "{http://www.alfresco.org/model/content/1.0}");
		parameterMap.put(Parameters.PARAM_CONTENTTYPE, "content");
		parameterMap.put(Parameters.PARAM_RELATIVE_PATH, "");
		Map<String, String> metadataMap = new HashMap<String, String>();
		metadataMap.put("{http://www.alfresco.org/model/content/1.0}description", "time:"+ new Date());
		parameterMap.put(Parameters.PARAM_METADATA, metadataMap);
	}

	@Override
	public String getName() {
		return "DummyParser";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "test123";
	}

	@Override
	public String getCategory() {
		return ConfigurableObject.CAT_METADATA;
	}

}
