/**
 * 
 */
package eu.xenit.move2alf.logic;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.ReportMessage;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.web.dto.JobConfig;

public class SuccessHandler {
	/**
	 * 
	 */
	private final JobService jobService;

	/**
	 * @param jobExecutionServiceImpl
	 */
	SuccessHandler(JobService jobService) {
		this.jobService = jobService;
	}
	
	private JobService getJobService() {
		return this.jobService;
	}

	public void handleSuccess(FileInfo parameterMap, JobConfig jobConfig,
			Cycle cycle) {
		File file = (File) parameterMap.get(Parameters.PARAM_FILE);

		// reporting
		Set<ProcessedDocumentParameter> params = createProcessedDocumentParameterSet(
				(Map<String, String>) parameterMap
						.get(Parameters.PARAM_REPORT_FIELDS), cycle
						.getSchedule().getJob().getFirstConfiguredAction());
		getJobService().getReportActor().sendOneWay(
				new ReportMessage(cycle.getId(), file.getName(),
						new Date(), Parameters.VALUE_OK, params));

		// move
		if (jobConfig.getMoveAfterLoad()) {
			String inputFolder = (String) parameterMap
					.get(Parameters.PARAM_INPUT_PATH);
			Util.moveFile(inputFolder, jobConfig.getMoveAfterLoadText(), file);
		}
	}
	
	Set<ProcessedDocumentParameter> createProcessedDocumentParameterSet(
			Map<String, String> reportFields, ConfiguredAction configuredAction) {
		Set<ProcessedDocumentParameter> procDocParameters = new HashSet<ProcessedDocumentParameter>();
		if (reportFields != null) {
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
		}
		return procDocParameters;
	}
}