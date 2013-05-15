package eu.xenit.move2alf.logic;

import eu.xenit.move2alf.pipeline.JobController;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.enums.ECycleState;
import eu.xenit.move2alf.logic.usageservice.UsageService;

public class JobExecutor implements org.quartz.Job {

	private static final Logger logger = LoggerFactory
			.getLogger(JobExecutor.class);

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		Util.authenticateAsSystem();
		Integer jobId = (Integer) context.getMergedJobDataMap().get(
				SchedulerImpl.JOB_ID);
		JobService jobService = (JobService) context.getMergedJobDataMap().get(
				SchedulerImpl.JOB_SERVICE);

		UsageService usageService = (UsageService) context.getMergedJobDataMap().get(SchedulerImpl.USAGE_SERVICE);

		if (checkLicense(usageService)) {
				executeJob(jobId, jobService);
		}
	}

	private boolean checkLicense(UsageService usageService) {
		return usageService.isValid();
	}

	private void executeJob(Integer jobId, JobService jobService) {
		Job job = null;
		Cycle cycle = null;

		try {
			logger.debug("looking for job");
			job = jobService.getJob(jobId);
			logger.debug("The job exists");
		} catch (Move2AlfException e) {
			logger.error("Could not execute job with id " + jobId
					+ " because the job does not exist.");
			e.printStackTrace();
			return;
		}

		if (jobService.getJobState(job.getId()).equals(ECycleState.RUNNING)) {
			logger.warn("Job \"" + job.getName()
					+ "\" already running, not starting second cycle");
			return;
		}

        JobController.startJob(jobId.toString());
    }

}