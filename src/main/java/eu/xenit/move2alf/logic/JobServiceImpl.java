package eu.xenit.move2alf.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.common.exceptions.NonexistentUserException;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSinkParameter;
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
	public List<Cycle> getCyclesForJob(int jobId) {
		return getSessionFactory()
				.getCurrentSession()
				.createQuery(
						"from Cycle as c where c.schedule.job.id=? order by c.endDateTime asc")
				.setLong(0, jobId).list();
	}

	@Override
	public List<Cycle> getLastCycleForJobs() {
		List<Job> allJobs = getAllJobs();

		List<Cycle> jobCycles = new ArrayList();

		List<Cycle> lastCycles = new ArrayList();

		for (int i = 0; i < allJobs.size(); i++) {
			jobCycles = getCyclesForJob(allJobs.get(i).getId());

			if (jobCycles.size() == 0) {
				lastCycles.add(null);
			} else {
				if (jobCycles.get(0).getEndDateTime() == null) {
					lastCycles.add(jobCycles.get(0));
				} else {
					lastCycles.add(jobCycles.get(jobCycles.size() - 1));
				}
			}
		}

		return lastCycles;
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
		getSessionFactory().getCurrentSession().evict(job); // job object is still in cache with old schedules
		getScheduler().reloadSchedules();

		return schedule;
	}

	@Override
	public void deleteSchedule(int scheduleId) {
		Schedule schedule = getSchedule(scheduleId);
		Job job = schedule.getJob();
		sessionFactory.getCurrentSession().delete(schedule);

		logger.debug("Reloading scheduler");
		getSessionFactory().getCurrentSession().evict(job); // job object is still in cache with old schedules
		getSessionFactory().getCurrentSession().flush();
		getScheduler().reloadSchedules();
	}

	@Override
	public ConfiguredSourceSink createDestination(String destinationType,
			HashMap destinationParams) {

		ConfiguredSourceSink sourceSink = new ConfiguredSourceSink();

		sourceSink.setSourceSinkClassName(destinationType);

		ConfiguredSourceSinkParameter sourceSinkName = new ConfiguredSourceSinkParameter();
		sourceSinkName.setConfiguredSourceSink(sourceSink);
		sourceSinkName.setName("name");
		sourceSinkName.setValue((String) destinationParams
				.get(EDestinationParameter.NAME));
		ConfiguredSourceSinkParameter sourceSinkURL = new ConfiguredSourceSinkParameter();
		sourceSinkURL.setConfiguredSourceSink(sourceSink);
		sourceSinkURL.setName("url");
		sourceSinkURL.setValue((String) destinationParams
				.get(EDestinationParameter.URL));
		ConfiguredSourceSinkParameter sourceSinkUser = new ConfiguredSourceSinkParameter();
		sourceSinkUser.setConfiguredSourceSink(sourceSink);
		sourceSinkUser.setName("user");
		sourceSinkUser.setValue((String) destinationParams
				.get(EDestinationParameter.USER));
		ConfiguredSourceSinkParameter sourceSinkPassword = new ConfiguredSourceSinkParameter();
		sourceSinkPassword.setConfiguredSourceSink(sourceSink);
		sourceSinkPassword.setName("password");
		sourceSinkPassword.setValue((String) destinationParams
				.get(EDestinationParameter.PASSWORD));
		ConfiguredSourceSinkParameter sourceSinkThreads = new ConfiguredSourceSinkParameter();
		sourceSinkThreads.setConfiguredSourceSink(sourceSink);
		sourceSinkThreads.setName("threads");
		sourceSinkThreads.setValue(destinationParams.get(
				EDestinationParameter.THREADS).toString());

		Set<ConfiguredSourceSinkParameter> parameterSet = new HashSet();
		parameterSet.add(sourceSinkName);
		parameterSet.add(sourceSinkURL);
		parameterSet.add(sourceSinkUser);
		parameterSet.add(sourceSinkPassword);
		parameterSet.add(sourceSinkThreads);

		sourceSink.setConfiguredSourceSinkParameterSet(parameterSet);

		getSessionFactory().getCurrentSession().save(sourceSink);

		return sourceSink;
	}

	@Override
	public ConfiguredSourceSink editDestination(int sinkId,
			String destinationType, HashMap destinationParams) {

		ConfiguredSourceSink sourceSink = getConfiguredSourceSink(sinkId);

		sourceSink.setSourceSinkClassName(destinationType);

		ConfiguredSourceSinkParameter sourceSinkName = new ConfiguredSourceSinkParameter();
		sourceSinkName.setConfiguredSourceSink(sourceSink);
		sourceSinkName.setName("name");
		sourceSinkName.setValue((String) destinationParams
				.get(EDestinationParameter.NAME));
		ConfiguredSourceSinkParameter sourceSinkURL = new ConfiguredSourceSinkParameter();
		sourceSinkURL.setConfiguredSourceSink(sourceSink);
		sourceSinkURL.setName("url");
		sourceSinkURL.setValue((String) destinationParams
				.get(EDestinationParameter.URL));
		ConfiguredSourceSinkParameter sourceSinkUser = new ConfiguredSourceSinkParameter();
		sourceSinkUser.setConfiguredSourceSink(sourceSink);
		sourceSinkUser.setName("user");
		sourceSinkUser.setValue((String) destinationParams
				.get(EDestinationParameter.USER));
		ConfiguredSourceSinkParameter sourceSinkPassword = new ConfiguredSourceSinkParameter();
		sourceSinkPassword.setConfiguredSourceSink(sourceSink);
		sourceSinkPassword.setName("password");
		sourceSinkPassword.setValue((String) destinationParams
				.get(EDestinationParameter.PASSWORD));
		ConfiguredSourceSinkParameter sourceSinkThreads = new ConfiguredSourceSinkParameter();
		sourceSinkThreads.setConfiguredSourceSink(sourceSink);
		sourceSinkThreads.setName("threads");
		sourceSinkThreads.setValue(destinationParams.get(
				EDestinationParameter.THREADS).toString());

		Set<ConfiguredSourceSinkParameter> parameterSet = new HashSet();
		parameterSet.add(sourceSinkName);
		parameterSet.add(sourceSinkURL);
		parameterSet.add(sourceSinkUser);
		parameterSet.add(sourceSinkPassword);
		parameterSet.add(sourceSinkThreads);

		sourceSink.setConfiguredSourceSinkParameterSet(parameterSet);

		getSessionFactory().getCurrentSession().save(sourceSink);

		return sourceSink;
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
	public void executeJob(int scheduleId) {
		Schedule schedule;
		Job job;
		try {
			schedule = getSchedule(scheduleId);
			job = schedule.getJob();
		} catch (Move2AlfException e) {
			logger.error("Could not execute job with schedule ID " + scheduleId
					+ " because schedule or job does not exist.");
			return;
		}
		logger.debug("Executing job \"" + job.getName() + "\"");

		Cycle cycle = new Cycle();
		cycle.setSchedule(schedule);
		cycle.setStartDateTime(new Date());

		// TODO: status

		Map<String, Object> parameterMap = new HashMap<String, Object>();
		ConfiguredAction action = job.getFirstConfiguredAction();
		while (action != null) {
			// TODO: set running action(s) on cycle
			try {
				action.execute(parameterMap);
				// TODO: status?
			} catch (Exception e) {
				action.getAppliedConfiguredActionOnFailure().execute(
						parameterMap);
				// TODO: status
				break;
			}
			action = action.getAppliedConfiguredActionOnSuccess();
		}

		// TODO: status?

		cycle.setEndDateTime(new Date());
		getSessionFactory().getCurrentSession().save(cycle);
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

		Set emptySet = new HashSet();

		destination.setConfiguredSourceSinkParameterSet(emptySet);

		sessionFactory.getCurrentSession().delete(destination);
	}

}
