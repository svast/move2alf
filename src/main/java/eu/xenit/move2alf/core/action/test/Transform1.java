package eu.xenit.move2alf.core.action.test;

import java.util.Map;

import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public class Transform1 extends Action {

	protected String name = "Metadata2";
	protected String description = "Description of metadata 2 action";
	protected String category = "metadata";
	
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
		return "Description of transformation action";
	}

	@Override
	public String getDescription() {
		return ConfigurableObject.CAT_TRANSFORM;
	}
}
