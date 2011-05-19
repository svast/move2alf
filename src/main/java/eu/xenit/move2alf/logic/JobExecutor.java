package eu.xenit.move2alf.logic;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
		SessionFactory sessionFactory = (SessionFactory) context
				.getMergedJobDataMap().get(SchedulerImpl.SESSION_FACTORY);

		//openSession(sessionFactory);

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

		//closeSession(sessionFactory);
	}

	private void openSession(SessionFactory sessionFactory) {
		Session session = null;
		try {
			session = SessionFactoryUtils.getSession(sessionFactory, false);
		}
		// If not already bound the Create and Bind it!
		catch (java.lang.IllegalStateException ex) {
			session = SessionFactoryUtils.getSession(sessionFactory, true);
			TransactionSynchronizationManager.bindResource(sessionFactory,
					new SessionHolder(session));
		}
		session.setFlushMode(FlushMode.AUTO);
	}

	private void closeSession(SessionFactory sessionFactory) {
		try {
			SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager
					.unbindResource(sessionFactory);
			if (!FlushMode.MANUAL.equals(sessionHolder.getSession()
					.getFlushMode())) {
				sessionHolder.getSession().flush();
			}
			SessionFactoryUtils.closeSession(sessionHolder.getSession());
			if (logger.isDebugEnabled())
				logger
						.debug("Hibernate Session is unbounded from Job thread and closed");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}