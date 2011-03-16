package eu.xenit.move2alf.core.action.test;

import java.util.Map;

import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public class Metadata2 extends Action {

	protected String name = "Metadata1";
	protected String description = "Description of metadata 1 action";
	protected String category = "metadata";
	
	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return "Metadata #1";
	}

	@Override
	public String getCategory() {
		return ConfigurableObject.CAT_METADATA;
	}

	@Override
	public String getDescription() {
		return "Description of metadata #1 action";
	}
}
