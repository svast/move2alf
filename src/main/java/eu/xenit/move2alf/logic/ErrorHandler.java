/**
 * 
 */
package eu.xenit.move2alf.logic;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.ReportMessage;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.web.dto.JobConfig;

public class ErrorHandler {

	private final JobService jobService;
	
	/**
	 * @param jobExecutionServiceImpl
	 */
	ErrorHandler(JobService jobService) {
		this.jobService = jobService;
	}
	
	private JobService getJobService() {
		return this.jobService;
	}

	public void handleError(FileInfo parameterMap, JobConfig jobConfig,
			Cycle cycle, Exception e) {
		// TODO: handle cleaner?
		File file = (File) parameterMap.get(Parameters.PARAM_FILE);

		// reporting
		Set<ProcessedDocumentParameter> params = new HashSet<ProcessedDocumentParameter>();
		ProcessedDocumentParameter msg = new ProcessedDocumentParameter();
		msg.setName(Parameters.PARAM_ERROR_MESSAGE);
		msg.setValue(e.getClass().getName() + ": " + e.getMessage());
		// Report everything using the first (deprecated) ConfiguredAction
		// of
		// the job.
		msg.setConfiguredAction(cycle.getSchedule().getJob()
				.getFirstConfiguredAction());
		params.add(msg);
		getJobService().getReportActor().sendOneWay(
				new ReportMessage(cycle.getId(), file.getName(),
						new Date(), Parameters.VALUE_FAILED, params));

		// move
		if ("true".equals(jobConfig.getMoveNotLoad())) {
			String inputFolder = (String) parameterMap
					.get(Parameters.PARAM_INPUT_PATH);
			Util.moveFile(inputFolder, jobConfig.getMoveNotLoadText(), file);
		}
	}
}