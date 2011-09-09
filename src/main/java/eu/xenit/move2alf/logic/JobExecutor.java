package eu.xenit.move2alf.logic;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.Schedule;
import eu.xenit.move2alf.core.enums.EScheduleState;

public class JobExecutor implements org.quartz.Job {

	private static final Logger logger = LoggerFactory.getLogger(JobExecutor.class);
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		Integer scheduleId = (Integer) context.getMergedJobDataMap().get(
				SchedulerImpl.SCHEDULE_ID);
		JobService jobService = (JobService) context.getMergedJobDataMap().get(
				SchedulerImpl.JOB_SERVICE);
		JobExecutionService jobExecutionService = (JobExecutionService) context.getMergedJobDataMap().get(
				SchedulerImpl.JOB_EXECUTION_SERVICE);
		
		Schedule schedule;
		Job job;
		Cycle cycle;

		try {
			schedule = jobService.getSchedule(scheduleId);
			job = schedule.getJob();
		} catch (Move2AlfException e) {
			logger.error("Could not execute job with schedule ID " + scheduleId
					+ " because schedule or job does not exist.");
			return;
		}

		if (jobService.getJobState(job.getId()).equals(
				EScheduleState.RUNNING)) {
			logger.warn("Job \"" + job.getName()
					+ "\" already running, not starting second cycle");
			return;
		}

		cycle = jobExecutionService.openCycleForSchedule(scheduleId);
		jobExecutionService.executeJobSteps(job, cycle);
		jobExecutionService.closeCycle(cycle);
	}

}