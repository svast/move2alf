package eu.xenit.move2alf.logic;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.ActionFactory;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;

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
		try {
			cycle = jobService.openCycleForSchedule(scheduleId);
		} catch (Move2AlfException e) {
			logger.error("Could not execute job with schedule ID " + scheduleId
					+ " because schedule or job does not exist.");
			return;
		}

		Job job = cycle.getSchedule().getJob();

		Map<String, Object> parameterMap = new HashMap<String, Object>();
		ConfiguredAction action = job.getFirstConfiguredAction();
		jobService.executeAction(action, parameterMap);

		jobService.closeCycle(cycle);
	}

}