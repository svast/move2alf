package eu.xenit.move2alf.core.scheduler;

import org.hibernate.SessionFactory;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.Schedule;
import eu.xenit.move2alf.logic.JobService;

public class Scheduler {
	private static final Logger logger = LoggerFactory
			.getLogger(Scheduler.class);

	private JobService jobService;
	
	private SessionFactory sessionFactory;

	private org.quartz.Scheduler scheduler;

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

	public void init() {
		Util.authenticateAsSystem();
		// Start Quartz
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
				
			}
		}
	}


	// shutdown
}
