package eu.xenit.move2alf.core.action;



import java.util.HashMap;
import java.util.Map;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public class FredModelDemoParser extends Action {

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		parameterMap.put(Parameters.PARAM_NAMESPACE, "{http://www.xenit.eu/fred/example/model/0.1}");
		parameterMap.put(Parameters.PARAM_CONTENTTYPE, "product");
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("product_name", "Move2Alf");
		parameterMap.put(Parameters.PARAM_METADATA, metadata);
	}

	@Override
	public String getCategory() {
		return ConfigurableObject.CAT_METADATA;
	}

	@Override
	public String getDescription() {
		return "Fred example model demo parser";
	}

	@Override
	public String getName() {
		return "Demo";
	}

}
