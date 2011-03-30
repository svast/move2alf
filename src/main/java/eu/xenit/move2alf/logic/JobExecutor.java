package eu.xenit.move2alf.logic;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.Schedule;
import eu.xenit.move2alf.core.enums.EScheduleState;

public class JobExecutor implements org.quartz.Job {
	private static final Logger logger = LoggerFactory
			.getLogger(JobExecutor.class);

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		Integer scheduleId = (Integer) context.getMergedJobDataMap().get(
				SchedulerImpl.SCHEDULE_ID);
		JobService jobService = (JobService) context.getMergedJobDataMap().get(
				SchedulerImpl.JOB_SERVICE);

		Cycle cycle;
		Job job;
		try {
			Schedule schedule = jobService.getSchedule(scheduleId);
			job = jobService.getJob(schedule.getJob().getId());
			if (jobService.getJobState(job.getId()).equals(
					EScheduleState.RUNNING)) {
				logger.warn("Job \"" + job.getName()
						+ "\" already running, not starting second cycle");
				return;
			}
			cycle = jobService.openCycleForSchedule(scheduleId);
		} catch (Move2AlfException e) {
			logger.error("Could not execute job with schedule ID " + scheduleId
					+ " because schedule or job does not exist.");
			return;
		}

		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put(Parameters.PARAM_CYCLE, cycle.getId());
		ConfiguredAction action = job.getFirstConfiguredAction();
		jobService.executeAction(cycle.getId(), action, parameterMap);

	}

}