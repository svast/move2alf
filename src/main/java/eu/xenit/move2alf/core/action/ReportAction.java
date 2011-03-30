package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.Date;
import java.util.Map;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public class ReportAction extends Action {

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		Integer cycleId = (Integer) parameterMap.get(Parameters.PARAM_CYCLE);
		String name = ((File) parameterMap.get(Parameters.PARAM_FILE)).getName();
		String state = (String) parameterMap.get(Parameters.PARAM_STATUS);
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
