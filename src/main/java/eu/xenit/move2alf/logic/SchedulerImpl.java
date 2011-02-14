package eu.xenit.move2alf.logic;

import javax.annotation.PostConstruct;

import org.hibernate.SessionFactory;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.Schedule;

@Service("scheduler")
public class SchedulerImpl extends AbstractHibernateService implements Scheduler {
	private static final Logger logger = LoggerFactory
			.getLogger(SchedulerImpl.class);

	private JobService jobService;
	
	private SessionFactory sessionFactory;

	private org.quartz.Scheduler scheduler;

	private static final String JOB_ID = "jobId";
	
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
			scheduler.start();
		} catch (SchedulerException e) {
			logger.error("Failed to create scheduler");
			e.printStackTrace();
			return;
		}
		for (Job job : getJobService().getAllJobs()) {
			for (Schedule schedule : job.getSchedules()) {
				String cronExpression = schedule.getQuartzScheduling();
				System.out.println("Adding schedule: " + cronExpression);
				JobDetail jobDetail = new JobDetail("Schedule-" + job.getName() + "-" + schedule.getId(), JobExecutor.class);
			}
		}
	}

	class JobExecutor implements org.quartz.Job {

		@Override
		public void execute(JobExecutionContext context)
				throws JobExecutionException {
			Integer jobId = (Integer) context.get(JOB_ID);
			getJobService().executeJob(jobId);
		}
		
	}
}
