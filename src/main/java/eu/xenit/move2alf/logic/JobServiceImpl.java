package eu.xenit.move2alf.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.ActionFactory;
import eu.xenit.move2alf.core.ConfiguredObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.Schedule;
import eu.xenit.move2alf.core.enums.EDestinationParameter;
import eu.xenit.move2alf.core.enums.EScheduleState;

@Service("jobService")
public class JobServiceImpl extends AbstractHibernateService implements
		JobService {
	private static final Logger logger = LoggerFactory
			.getLogger(JobServiceImpl.class);

	private UserService userService;

	private Scheduler scheduler;

	private ActionFactory actionFactory;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserService getUserService() {
		return userService;
	}

	@Autowired
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	@Autowired
	public void setActionFactory(ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
	}

	public ActionFactory getActionFactory() {
		return actionFactory;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Job> getAllJobs() {
		return getSessionFactory().getCurrentSession().createQuery("from Job")
				.list();
	}

	@Override
	public Job createJob(String name, String description) {
		Date now = new Date();
		Job job = new Job();
		job.setName(name);
		job.setDescription(description);
		job.setCreationDateTime(now);
		job.setLastModifyDateTime(now);
		job.setCreator(getUserService().getCurrentUser());
		getSessionFactory().getCurrentSession().save(job);
		return job;
	}

	@Override
	public Job editJob(int id, String name, String description) {
		Date now = new Date();
		Job job = getJob(id);
		job.setId(id);
		job.setName(name);
		job.setDescription(description);
		job.setCreationDateTime(now);
		job.setLastModifyDateTime(now);
		job.setCreator(getUserService().getCurrentUser());
		getSessionFactory().getCurrentSession().save(job);
		return job;
	}

	@Override
	public void deleteJob(int id) {
		Job job = getJob(id);
		sessionFactory.getCurrentSession().delete(job);

		logger.debug("Reloading scheduler");
		getSessionFactory().getCurrentSession().flush();
		getScheduler().reloadSchedules();
	}

	@Override
	public Job getJob(int id) {
		@SuppressWarnings("unchecked")
		List jobs = sessionFactory.getCurrentSession().createQuery(
				"from Job as j where j.id=?").setLong(0, id).list();
		if (jobs.size() == 1) {
			return (Job) jobs.get(0);
		} else {
			throw new Move2AlfException("Job with id " + id + " not found."); // TODO:
			// exception
			// type??
		}
	}

	@Override
	public Cycle getCycle(int cycleId) {
		List<Cycle> cycles = getSessionFactory().getCurrentSession()
				.createQuery("from Cycle as c where c.id=?")
				.setLong(0, cycleId).list();

		return cycles.get(0);
	}

	@Override
	public List<Cycle> getCyclesForJob(int jobId) {
		return getSessionFactory()
				.getCurrentSession()
				.createQuery(
						"from Cycle as c where c.schedule.job.id=? order by c.endDateTime asc")
				.setLong(0, jobId).list();
	}

	@Override
	public List<Cycle> getCyclesForJobDesc(int jobId) {
		return getSessionFactory()
				.getCurrentSession()
				.createQuery(
						"from Cycle as c where c.schedule.job.id=? order by c.endDateTime desc")
				.setLong(0, jobId).list();
	}

	@Override
	public Cycle getLastCycleForJob(Job job) {
		List<Job> allJobs = getAllJobs();

		List<Cycle> jobCycles = new ArrayList();

		Cycle lastCycle;

		jobCycles = getCyclesForJob(job.getId());

		if (jobCycles.size() == 0) {
			lastCycle = null;
		} else {
			if (jobCycles.get(0).getEndDateTime() == null) {
				lastCycle = jobCycles.get(0);
			} else {
				lastCycle = jobCycles.get(jobCycles.size() - 1);
			}
		}

		return lastCycle;
	}

	@Override
	public List<Schedule> getSchedulesForJob(int jobId) {
		@SuppressWarnings("unchecked")
		List schedules = sessionFactory.getCurrentSession().createQuery(
				"from Schedule as s where s.job.id=?").setLong(0, jobId).list();
		return (List<Schedule>) schedules;
	}

	@Override
	public Schedule getSchedule(int scheduleId) {
		@SuppressWarnings("unchecked")
		List schedules = sessionFactory.getCurrentSession().createQuery(
				"from Schedule as s where s.id=?").setLong(0, scheduleId)
				.list();
		if (schedules.size() == 1) {
			return (Schedule) schedules.get(0);
		} else {
			throw new Move2AlfException("Schedule with id " + scheduleId
					+ " not found.");
		}
	}

	@Override
	public int getScheduleId(int jobId, String cronJob) {
		@SuppressWarnings("unchecked")
		List schedule = sessionFactory.getCurrentSession().createQuery(
				"from Schedule as s where s.job.id=? and s.quartzScheduling=?")
				.setLong(0, jobId).setString(1, cronJob).list();

		return ((Schedule) schedule.get(0)).getId();
	}

	@Override
	public List<String> getCronjobsForJob(int jobId) {
		List<Schedule> schedules = getSchedulesForJob(jobId);
		List<String> cronjobs = new ArrayList();

		for (int i = 0; i < schedules.size(); i++) {
			cronjobs.add(schedules.get(i).getQuartzScheduling());
		}
		return cronjobs;
	}

	@Override
	public Schedule createSchedule(int jobId, String cronJob) {
		Date now = new Date();
		Schedule schedule = new Schedule();
		Job job = getJob(jobId);
		schedule.setJob(job);
		schedule.setCreator(getUserService().getCurrentUser());
		schedule.setCreationDateTime(now);
		schedule.setLastModifyDateTime(now);
		schedule.setQuartzScheduling(cronJob);
		schedule.setState(EScheduleState.NOT_RUNNING);
		schedule.setStartDateTime(now);
		schedule.setEndDateTime(now);
		getSessionFactory().getCurrentSession().save(schedule);

		logger.debug("Reloading scheduler");
		getSessionFactory().getCurrentSession().evict(job); // job object is
		// still in cache
		// with old
		// schedules
		getScheduler().reloadSchedules();

		return schedule;
	}

	@Override
	public void deleteSchedule(int scheduleId) {
		Schedule schedule = getSchedule(scheduleId);
		Job job = schedule.getJob();
		sessionFactory.getCurrentSession().delete(schedule);

		logger.debug("Reloading scheduler");
		getSessionFactory().getCurrentSession().evict(job); // job object is
		// still in cache
		// with old
		// schedules
		getSessionFactory().getCurrentSession().flush();
		getScheduler().reloadSchedules();
	}

	@Override
	public ConfiguredObject createDestination(String destinationType,
			HashMap destinationParams) {
		ConfiguredSourceSink sourceSink = new ConfiguredSourceSink();
		createSourceSink(destinationType, destinationParams, sourceSink);
		getSessionFactory().getCurrentSession().save(sourceSink);
		return sourceSink;
	}

	@Override
	public ConfiguredObject editDestination(int sinkId, String destinationType,
			HashMap destinationParams) {
		ConfiguredSourceSink sourceSink = getConfiguredSourceSink(sinkId);
		sourceSink.setClassName(destinationType);
		createSourceSink(destinationType, destinationParams, sourceSink);
		getSessionFactory().getCurrentSession().save(sourceSink);
		return sourceSink;
	}

	private void createSourceSink(String destinationType,
			HashMap destinationParams, ConfiguredSourceSink sourceSink) {
		sourceSink.setClassName(destinationType);

		Map<String, String> sourceSinkParameters = new HashMap<String, String>();
		sourceSinkParameters.put("name", (String) destinationParams
				.get(EDestinationParameter.NAME));
		sourceSinkParameters.put("url", (String) destinationParams
				.get(EDestinationParameter.URL));
		sourceSinkParameters.put("user", (String) destinationParams
				.get(EDestinationParameter.USER));
		sourceSinkParameters.put("password", (String) destinationParams
				.get(EDestinationParameter.PASSWORD));
		sourceSinkParameters.put("threads", (String) destinationParams
				.get(EDestinationParameter.THREADS));
		sourceSink.setParameters(sourceSinkParameters);
	}

	@Override
	public List<ConfiguredSourceSink> getAllConfiguredSourceSinks() {
		@SuppressWarnings("unchecked")
		List<ConfiguredSourceSink> configuredSourceSink = sessionFactory
				.getCurrentSession().createQuery("from ConfiguredSourceSink")
				.list();

		return (List<ConfiguredSourceSink>) configuredSourceSink;
	}

	@Override
	public ConfiguredSourceSink getConfiguredSourceSink(int sourceSinkId) {
		@SuppressWarnings("unchecked")
		List<ConfiguredSourceSink> configuredSourceSink = sessionFactory
				.getCurrentSession().createQuery(
						"from ConfiguredSourceSink as c where c.id=?").setLong(
						0, sourceSinkId).list();
		if (configuredSourceSink.size() == 1) {
			return (ConfiguredSourceSink) configuredSourceSink.get(0);
		} else {
			throw new Move2AlfException("ConfiguredSourceSink with id "
					+ sourceSinkId + " not found");
		}
	}

	@Override
	public void deleteDestination(int id) {
		ConfiguredSourceSink destination = getConfiguredSourceSink(id);
		Map<String, String> emptyMap = new HashMap<String, String>();
		destination.setParameters(emptyMap);
		sessionFactory.getCurrentSession().delete(destination);
	}

	@Override
	public void closeCycle(Cycle cycle) {
		Session session = getSessionFactory().getCurrentSession();

		Schedule schedule = cycle.getSchedule();
		schedule.setState(EScheduleState.NOT_RUNNING);
		session.update(schedule);

		cycle.setEndDateTime(new Date());
		session.update(cycle);
	}

	@Override
	public Cycle openCycleForSchedule(Integer scheduleId) {
		Session session = getSessionFactory().getCurrentSession();

		Schedule schedule = getSchedule(scheduleId);
		Job job = schedule.getJob();
		logger.debug("Executing job \"" + job.getName() + "\"");

		Cycle cycle = new Cycle();
		cycle.setSchedule(schedule);
		cycle.setStartDateTime(new Date());
		session.save(cycle);

		schedule.setState(EScheduleState.RUNNING);
		session.update(schedule);

		return cycle;
	}

	public String getDuration(Date startDateTime, Date endDateTime) {
		Long duration = endDateTime.getTime() - startDateTime.getTime();
		Date dateDuration = new Date(duration);

		Calendar cal = Calendar.getInstance();
		cal.setTime(dateDuration);

		int date = cal.get(Calendar.DATE) - 1;
		int hours = cal.get(Calendar.HOUR_OF_DAY) - 1;
		int minutes = cal.get(Calendar.MINUTE);
		int seconds = cal.get(Calendar.SECOND);

		if (date > 0) {
			hours = hours + date * 24;
		}

		String hoursString = Integer.toString(hours);
		String minutesString = Integer.toString(minutes);
		String secondsString = Integer.toString(seconds);

		if (hoursString.length() < 2)
			hoursString = "0" + hours;
		if (minutesString.length() < 2)
			minutesString = "0" + minutes;
		if (secondsString.length() < 2)
			secondsString = "0" + seconds;

		String durationDateString = hoursString + ":" + minutesString + ":"
				+ secondsString;

		return durationDateString;
	}

	@Override
	public void executeJob(int scheduleId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeAction(ConfiguredAction action,
			Map<String, Object> parameterMap) {
		logger.debug("Executing action: " + action.getId() + " - "
				+ action.getClassName());
		getActionFactory().execute(action, parameterMap);
	}

}
