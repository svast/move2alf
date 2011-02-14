/**
 * 
 */
package eu.xenit.move2alf.logic;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobExecutor implements org.quartz.Job {
	private static final Logger logger = LoggerFactory
			.getLogger(JobExecutor.class);

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		Integer jobId = (Integer) context.getMergedJobDataMap().get(SchedulerImpl.JOB_ID);
		logger.debug("executing: " + jobId); // tmp 
		// getJobService().executeJob(jobId);
	}

}