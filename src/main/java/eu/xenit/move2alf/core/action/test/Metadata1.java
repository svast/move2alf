package eu.xenit.move2alf.core.action.test;

import java.util.Map;

import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public class Metadata1 extends Action {

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return "Transform";
	}

	@Override
	public String getCategory() {
		return ConfigurableObject.CAT_METADATA;
	}

	@Override
	public String getDescription() {
		return "Description of transformation action";
	}

}
