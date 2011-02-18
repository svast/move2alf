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
		ActionFactory actionFactory = (ActionFactory) context
				.getMergedJobDataMap().get(SchedulerImpl.ACTION_FACTORY);

		// TODO: extract job execution responsibility to separate class

		Cycle cycle;
		try {
			cycle = jobService.openCycleForSchedule(scheduleId);
		} catch (Move2AlfException e) {
			logger.error("Could not execute job with schedule ID " + scheduleId
					+ " because schedule or job does not exist.");
			return;
		}

		Job job = cycle.getSchedule().getJob();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		Map<String, Object> parameterMap = new HashMap<String, Object>();
		ConfiguredAction action = job.getFirstConfiguredAction();
		while (action != null) {
			// TODO: set running action(s) on cycle
			try {
				logger.debug("Executing action: " + action.getId() + " - "
						+ action.getActionClassName());
				actionFactory.getAction(action.getActionClassName()).execute(
						action, parameterMap);
				// TODO: status?
			} catch (Exception e) {
				e.printStackTrace();
				// action.getAppliedConfiguredActionOnFailure().execute(
				// parameterMap);
				// TODO: status
				break;
			}
			action = action.getAppliedConfiguredActionOnSuccess();
		}

		jobService.closeCycle(cycle);
	}

}