package eu.xenit.move2alf.logic;

import java.text.ParseException;
import java.util.Calendar;

import javax.annotation.PostConstruct;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.Schedule;

@Service("scheduler")
public class SchedulerImpl extends AbstractHibernateService implements
		Scheduler {
	private static final Logger logger = LoggerFactory
			.getLogger(SchedulerImpl.class);

	private JobService jobService;
	
	private JobExecutionService jobExecutionService;
	
	private UsageService usageService;

	private org.quartz.Scheduler scheduler;

	static final String SCHEDULE_ID = "jobId";
	
	static final String JOB_SERVICE = "jobService";

	static final String JOB_EXECUTION_SERVICE = "jobExecutionService";
	
	static final String USAGE_SERVICE = "usageService";

	public static final String DEFAULT_SCHEDULE = "0 0 0 1 1 ? 1";


	@Autowired
	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}

	public JobService getJobService() {
		return jobService;
	}

	@Autowired
	public void setJobExecutionService(JobExecutionService jobExecutionService) {
		this.jobExecutionService = jobExecutionService;
	}

	public JobExecutionService getJobExecutionService() {
		return jobExecutionService;
	}

	@Autowired
	public void setUsageService(UsageService usageService) {
		this.usageService = usageService;
	}
	
	public UsageService getUsageService() {
		return this.usageService;
	}
	
	@PostConstruct
	public void init() {
		logger.debug("Initializing scheduler");
		Util.authenticateAsSystem();
		reloadSchedules();
	}

	public void reloadSchedules() {
		getJobService().resetSchedules();
		logger.debug("Reloading schedules");
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			if (scheduler.isStarted()) {
				// stop previous scheduler and get new one
				scheduler.shutdown();
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
				JobDetail jobDetail = new JobDetail("Job-" + job.getId() + schedule.getId(), JobExecutor.class);
				String cronExpression = schedule.getQuartzScheduling();
				logger.debug("Adding schedule " + cronExpression);
				try {
					Trigger trigger = new CronTrigger("Trigger-"
							+ schedule.getId(), "JobScheduleGroup",
							cronExpression);
					JobDataMap jobData = createJobDataMap(schedule.getId());
					trigger.setJobDataMap(jobData);
					scheduler.scheduleJob(jobDetail, trigger);
				} catch (SchedulerException schedulerException) {
					logger.warn("Scheduling job \"" + job.getName()
							+ "\" failed");
					// schedulerException.printStackTrace();
					// keep
					// "org.quartz.SchedulerException: Based on configured schedule, the given trigger will never fire."
					// stack traces out of the logs
				} catch (ParseException parseException) {
					logger.error("Parsing of cron expression for job \""
							+ job.getName() + "\" failed: " + cronExpression);
					parseException.printStackTrace();
				}
			}
		}
	}

	private JobDataMap createJobDataMap(int scheduleId) {
		JobDataMap jobData = new JobDataMap();
		jobData.put(SCHEDULE_ID, scheduleId);
		jobData.put(JOB_SERVICE, getJobService());
		jobData.put(JOB_EXECUTION_SERVICE, getJobExecutionService());
		jobData.put(USAGE_SERVICE, getUsageService());
		return jobData;
	}

	@Override
	public void immediately(Job job, int scheduleId) {
		logger.debug("Scheduling immediate job: " + job.getName());
		
		String jobId = "Schedule-"
				+ job.getName() + "-" + job.getId() + "-"
				+ scheduleId + "-" + Calendar.getInstance().getTimeInMillis();
		JobDetail jobDetail = new JobDetail( jobId, JobExecutor.class);
		logger.debug("Configuring Scheduler Job with id: "+jobId);
		
		//Trigger fires as quick as possible
		//first argument 0 : no repeats
		//second argument is the interval between repeats, but irrelevant here
		Trigger trigger = TriggerUtils.makeImmediateTrigger(0, 1000);
		trigger.setName("Immediate trigger");
		trigger.setGroup("JobScheduleGroup");
		JobDataMap jobData = createJobDataMap(scheduleId);
		trigger.setJobDataMap(jobData);
		try {
			scheduler.scheduleJob(jobDetail, trigger);
			logger.debug("Trigger for schedule " + scheduleId + " fired!");
		} catch (SchedulerException e) {
			logger.error("Scheduling immediate job \"" + job.getName()
					+ "\" failed");
			e.printStackTrace();
		}
	}
}
