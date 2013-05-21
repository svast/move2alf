package eu.xenit.move2alf.logic;

import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.logic.usageservice.UsageService;
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
		jobService.startJob(jobId);
    }

}