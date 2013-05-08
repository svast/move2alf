/**
 * 
 */
package eu.xenit.move2alf.logic;

import java.io.File;
import java.util.*;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.ReportMessage;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.web.dto.JobConfig;

public class DefaultErrorHandler implements ErrorHandler {

	private final JobService jobService;
	
	/**
	 * @param jobService
	 */
	DefaultErrorHandler(JobService jobService) {
		this.jobService = jobService;
	}
	
	protected JobService getJobService() {
		return this.jobService;
	}

	/* (non-Javadoc)
	 * @see eu.xenit.move2alf.logic.ErrorHandlerInterface#handleError(eu.xenit.move2alf.core.simpleaction.data.FileInfo, eu.xenit.move2alf.web.dto.JobConfig, eu.xenit.move2alf.core.dto.Cycle, java.lang.Exception)
	 */
	@Override
	public void handleError(FileInfo parameterMap, JobConfig jobConfig,
			Cycle cycle, Exception e) {
		// TODO: handle cleaner?
		File file = (File) parameterMap.get(Parameters.PARAM_FILE);

		// reporting
		Set<ProcessedDocumentParameter> params = new HashSet<ProcessedDocumentParameter>();
		ProcessedDocumentParameter msg = new ProcessedDocumentParameter();

		final String errorMessage = Util.getFullErrorMessage(e);
		msg.setName(Parameters.PARAM_ERROR_MESSAGE);
		msg.setValue(errorMessage);
		// Report everything using the first (deprecated) ConfiguredAction of the job.
		msg.setConfiguredAction(cycle.getJob()
				.getFirstConfiguredAction());
		params.add(msg);
		
		getJobService().getReportActor().tell(
				new ReportMessage(cycle.getId(), file.getName(),
						new Date(), Parameters.VALUE_FAILED, params, (String)parameterMap.get(Parameters.PARAM_REFERENCE)));

		// move
		if (jobConfig.getMoveNotLoad()) {
			String inputFolder = (String) parameterMap
					.get(Parameters.PARAM_INPUT_PATH);
			Util.moveFile(inputFolder, jobConfig.getMoveNotLoadText(), file);
		}
	}

}