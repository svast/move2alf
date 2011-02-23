package eu.xenit.move2alf.logic;

import java.text.ParseException;

import javax.annotation.PostConstruct;

import org.hibernate.SessionFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.ActionFactory;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.Schedule;

@Service("scheduler")
public class SchedulerImpl extends AbstractHibernateService implements
		Scheduler {
	private static final Logger logger = LoggerFactory
			.getLogger(SchedulerImpl.class);

	private JobService jobService;

	private SessionFactory sessionFactory;

	private org.quartz.Scheduler scheduler;

	static final String SCHEDULE_ID = "jobId";

	static final String JOB_SERVICE = "jobService";

	@Autowired
	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}

	public JobService getJobService() {
		return jobService;
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@PostConstruct
	public void init() {
		logger.debug("Initializing scheduler");
		Util.authenticateAsSystem();
		reloadSchedules();
	}

	public void reloadSchedules() {
		logger.debug("Reloading schedules");
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			if (scheduler.isStarted()) {
				// stop previous scheduler and get new one
				scheduler.shutdown(true); // TODO: will this block creating new
				// schedules when jobs are running?
				scheduler = StdSchedulerFactory.getDefaultScheduler();
			}
			scheduler.start();
		} catch (SchedulerException e) {
			logger.error("Failed to create scheduler");
			e.printStackTrace();
			return;
		}
		for (Job job : getJobService().getAllJobs()) {
			logger.debug("Scheduling job: " + job.getName());
			for (Schedule schedule : job.getSchedules()) {
				String cronExpression = schedule.getQuartzScheduling();
				logger.debug("Adding schedule " + cronExpression);
				try {
					JobDetail jobDetail = new JobDetail("Schedule-"
							+ job.getName() + "-" + job.getId() + "-"
							+ schedule.getId(), JobExecutor.class);
					Trigger trigger = new CronTrigger("Trigger-"
							+ schedule.getId(), "JobScheduleGroup",
							cronExpression);
					JobDataMap jobData = new JobDataMap();
					jobData.put(SCHEDULE_ID, schedule.getId());
					jobData.put(JOB_SERVICE, getJobService());
					trigger.setJobDataMap(jobData);
					scheduler.scheduleJob(jobDetail, trigger);
				} catch (SchedulerException schedulerException) {
					logger.error("Scheduling job \"" + job.getName()
							+ "\" failed");
					schedulerException.printStackTrace();
				} catch (ParseException parseException) {
					logger.error("Parsing of cron expression for job \""
							+ job.getName() + "\" failed: " + cronExpression);
					parseException.printStackTrace();
				}
			}
		}
	}
}
