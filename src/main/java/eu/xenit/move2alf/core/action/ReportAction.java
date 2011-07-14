package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.ReportMessage;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;

public class ReportAction extends Action {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportAction.class);

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		Integer cycleId = (Integer) parameterMap.get(Parameters.PARAM_CYCLE);
		String name = ((File) parameterMap.get(Parameters.PARAM_FILE))
				.getName();
		String state = (String) parameterMap.get(Parameters.PARAM_STATUS);
		
		Map<String, String> reportFields = (Map<String, String>) parameterMap
				.get(Parameters.PARAM_REPORT_FIELDS);
		
		Set<ProcessedDocumentParameter> procDocParameters;
		if (reportFields == null) {
			reportFields = new HashMap<String, String>();
		}
		
		String errorMsg = (String) parameterMap
				.get(Parameters.PARAM_ERROR_MESSAGE);
		if (errorMsg != null) {
			reportFields.put("Error message", errorMsg);
		}
		
		procDocParameters = createProcessedDocumentParameterSet(reportFields, configuredAction);
		
		getJobService().getReportActor().sendOneWay(new ReportMessage(cycleId, name, new Date(), state, procDocParameters));
		
		// Writing reporting data to DB is handled by ReportActor
		//getJobService().createProcessedDocument(cycleId, name, new Date(),
		//		state, procDocParameters);
	}

	private Set<ProcessedDocumentParameter> createProcessedDocumentParameterSet(
			Map<String, String> reportFields, ConfiguredAction configuredAction) {
		Set<ProcessedDocumentParameter> procDocParameters = new HashSet<ProcessedDocumentParameter>();
		for (String key : reportFields.keySet()) {
			ProcessedDocumentParameter procDocParameter = new ProcessedDocumentParameter();
			procDocParameter.setName(key);
			String value = reportFields.get(key);
			if (value.length() > 255) {
				value = value.substring(0, 255);
			}
			procDocParameter.setValue(value);
			procDocParameter.setConfiguredAction(configuredAction);
			procDocParameters.add(procDocParameter);
		}
		return procDocParameters;
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
