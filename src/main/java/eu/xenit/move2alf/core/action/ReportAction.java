package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.Date;
import java.util.Map;

import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public class ReportAction extends Action {

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		Integer cycleId = (Integer) parameterMap.get("cycle");
		String name = ((File) parameterMap.get("file")).getName();
		String state = (String) parameterMap.get("status");
		getJobService().createProcessedDocument(cycleId, name, new Date(), state, null);
	}

	@Override
	public String getCategory() {
		return ConfigurableObject.CAT_DEFAULT;
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getName() {
		return "Report";
	}
	
}
