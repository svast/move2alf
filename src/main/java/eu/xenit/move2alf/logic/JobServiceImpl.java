package eu.xenit.move2alf.logic;

import static akka.actor.Actors.actorOf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import akka.actor.ActorRef;
import eu.xenit.move2alf.common.IdObject;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ActionFactory;
import eu.xenit.move2alf.core.ConfiguredObject;
import eu.xenit.move2alf.core.ReportActor;
import eu.xenit.move2alf.core.SourceSink;
import eu.xenit.move2alf.core.SourceSinkFactory;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.ProcessedDocument;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.core.dto.Schedule;
import eu.xenit.move2alf.core.enums.EDestinationParameter;
import eu.xenit.move2alf.core.enums.EProcessedDocumentStatus;
import eu.xenit.move2alf.core.enums.EScheduleState;
import eu.xenit.move2alf.web.dto.HistoryInfo;

@Service("jobService")
public class JobServiceImpl extends AbstractHibernateService implements
		JobService {
	private static final Logger logger = LoggerFactory
			.getLogger(JobServiceImpl.class);

	private UserService userService;

	private Scheduler scheduler;

	private ActionFactory actionFactory;

	private SourceSinkFactory sourceSinkFactory;

	private MailSender mailSender;

	private ActorRef reportActor;

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

	@Autowired
	public void setSourceSinkFactory(SourceSinkFactory sourceSinkFactory) {
		this.sourceSinkFactory = sourceSinkFactory;
	}

	public SourceSinkFactory getSourceSinkFactory() {
		return sourceSinkFactory;
	}

	@Autowired
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public MailSender getMailSender() {
		return mailSender;
	}

	public ActorRef getReportActor() {
		return reportActor;
	}

	public JobServiceImpl() {
		reportActor = actorOf(ReportActor.class).start();
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
			throw new Move2AlfException("Job with id " + id + " not found.");
		}
	}

	@Override
	public boolean checkJobExists(String jobName) {
		@SuppressWarnings("unchecked")
		List jobs = sessionFactory.getCurrentSession().createQuery(
				"from Job as j where j.name=?").setString(0, jobName).list();
		return (jobs.size() > 0);
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
						"from Cycle as c where c.schedule.job.id=? order by c.startDateTime desc")
				.setLong(0, jobId).list();
	}

	@Override
	public Cycle getLastCycleForJob(Job job) {
		List<Job> allJobs = getAllJobs();

		List<Cycle> jobCycles = new ArrayList();

		Cycle lastCycle;
		jobCycles = getCyclesForJobDesc(job.getId());

		if (jobCycles.size() == 0) {
			lastCycle = null;
		} else {
			lastCycle = jobCycles.get(0);
		}

		/*
		 * jobCycles = getCyclesForJob(job.getId());
		 * 
		 * if (jobCycles.size() == 0) { lastCycle = null; } else { if
		 * (jobCycles.get(0).getEndDateTime() == null) { lastCycle =
		 * jobCycles.get(0); } else { lastCycle = jobCycles.get(jobCycles.size()
		 * - 1); } }
		 */

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
	@Transactional(noRollbackFor = IndexOutOfBoundsException.class)
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
		getSessionFactory().getCurrentSession().evict(job);
		// job object is still in cache with old schedules
		getScheduler().reloadSchedules();

		return schedule;
	}

	@Override
	public void deleteSchedule(int scheduleId) {
		Schedule schedule = getSchedule(scheduleId);
		Job job = schedule.getJob();
		sessionFactory.getCurrentSession().delete(schedule);

		logger.debug("Reloading scheduler");
		getSessionFactory().getCurrentSession().evict(job);
		// job object is still in cache with old schedules
		getSessionFactory().getCurrentSession().flush();
		getScheduler().reloadSchedules();
	}

	@Override
	public ConfiguredSourceSink createDestination(String destinationType,
			HashMap destinationParams) {
		ConfiguredSourceSink sourceSink = new ConfiguredSourceSink();
		createSourceSink(destinationType, destinationParams, sourceSink);
		getSessionFactory().getCurrentSession().save(sourceSink);
		return sourceSink;
	}

	@Override
	public ConfiguredSourceSink editDestination(int sinkId,
			String destinationType, HashMap destinationParams) {
		ConfiguredSourceSink sourceSink = getConfiguredSourceSink(sinkId);
		sourceSink.setClassName(destinationType);
		createSourceSink(destinationType, destinationParams, sourceSink);
		getSessionFactory().getCurrentSession().save(sourceSink);
		return sourceSink;
	}

	@Override
	public ConfiguredSourceSink getDestination(int id) {
		return (ConfiguredSourceSink) getSessionFactory().getCurrentSession()
				.get(ConfiguredSourceSink.class, id);
	}

	@Override
	public boolean checkDestinationExists(String destinationName) {
		@SuppressWarnings("unchecked")
		List destinations = sessionFactory.getCurrentSession().createQuery(
				"from ConfiguredSourceSink").list();

		for (int i = 0; i < destinations.size(); i++) {
			String destinationParameter = ((ConfiguredObject) destinations
					.get(i)).getParameter("name");

			if (destinationName.equals(destinationParameter)) {
				return true;
			}
		}
		return false;
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
		sourceSinkParameters.put("threads", destinationParams.get(
				EDestinationParameter.THREADS).toString());
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
	public List<ConfiguredSourceSink> getAllDestinationConfiguredSourceSinks() {
		@SuppressWarnings("unchecked")
		String fileSourceSink = "eu.xenit.move2alf.core.sourcesink.FileSourceSink";
		List<ConfiguredSourceSink> configuredSourceSink = sessionFactory
				.getCurrentSession().createQuery(
						"from ConfiguredSourceSink as c where c.className!=?")
				.setString(0, fileSourceSink).list();

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
	public ConfiguredAction getActionRelatedToConfiguredSourceSink(
			int sourceSinkId) {
		List<ConfiguredAction> configuredActions = sessionFactory
				.getCurrentSession().createQuery("from ConfiguredAction")
				.list();

		for (int i = 0; i < configuredActions.size(); i++) {
			Set<ConfiguredSourceSink> configuredSourceSinkForAction = configuredActions
					.get(i).getConfiguredSourceSinkSet();

			for (int j = 0; j < configuredSourceSinkForAction.size(); j++) {
				Iterator configuredSourceSinkIterator = configuredSourceSinkForAction
						.iterator();

				while (configuredSourceSinkIterator.hasNext()) {
					if (((IdObject) configuredSourceSinkIterator.next())
							.getId() == sourceSinkId) {
						return configuredActions.get(i);
					}
				}
			}
		}
		return null;
	}

	@Override
	public void deleteDestination(int id) {
		ConfiguredSourceSink destination = getConfiguredSourceSink(id);
		Map<String, String> emptyMap = new HashMap<String, String>();
		destination.setParameters(emptyMap);
		sessionFactory.getCurrentSession().delete(destination);
	}

	public String getDuration(Date startDateTime, Date endDateTime) {
		if (endDateTime == null) {
			endDateTime = new Date();
		}
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

	private Map<Integer, List<ConfiguredAction>> runningActions = Collections
			.synchronizedMap(new HashMap<Integer, List<ConfiguredAction>>());

	@Override
	public void executeAction(int cycleId, ConfiguredAction action,
			Map<String, Object> parameterMap) {
		throw new UnsupportedOperationException("Use JobExecutionService instead");
	}

	@Override
	public Map<String, String> getActionParameters(int cycleId,
			Class<? extends Action> clazz) {
		Cycle cycle = getCycle(cycleId);
		ConfiguredAction action = cycle.getSchedule().getJob()
				.getFirstConfiguredAction();
		while (action != null) {
			if (clazz.getName().equals(action.getClassName())) {
				return action.getParameters();
			}
			action = action.getAppliedConfiguredActionOnSuccess();
		}
		return null;
	}

	@Override
	public void addSourceSinkToAction(ConfiguredAction action,
			ConfiguredSourceSink sourceSink) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createAction(String className, Map<String, String> parameters) {
		ConfiguredAction action = new ConfiguredAction();
		action.setClassName(className);
		action.setParameters(parameters);
		getSessionFactory().getCurrentSession().save(action);
	}

	@Override
	public void createSourceSink(String className,
			Map<String, String> parameters) {
		ConfiguredSourceSink ss = new ConfiguredSourceSink();
		ss.setClassName(className);
		ss.setParameters(parameters);
		getSessionFactory().getCurrentSession().save(ss);
	}

	@Override
	public List<ProcessedDocument> getProcessedDocuments(int cycleId) {
		return (List<ProcessedDocument>) sessionFactory.getCurrentSession()
				.createQuery("from ProcessedDocument as d where d.cycle.id=?")
				.setLong(0, cycleId).list();
	}

	@Override
	public void deleteAction(int id) {
		Session s = getSessionFactory().getCurrentSession();
		s.delete(s.get(ConfiguredAction.class, id));
	}

	@Override
	public List<Action> getActionsByCategory(String category) {
		return getActionFactory().getObjectsByCategory(category);
	}

	@Override
	public List<SourceSink> getSourceSinksByCategory(String category) {
		return getSourceSinkFactory().getObjectsByCategory(category);
	}

	public EScheduleState getJobState(int jobId) {
		Job job = getJob(jobId);
		for (Schedule schedule : job.getSchedules()) {
			if (schedule.getState().equals(EScheduleState.RUNNING)) {
				return EScheduleState.RUNNING;
			}
		}
		return EScheduleState.NOT_RUNNING;
	}

	@Override
	public void resetSchedules() {
		org.hibernate.classic.Session session = getSessionFactory()
				.getCurrentSession();
		List<Schedule> schedules = session.createQuery("from Schedule").list();
		for (Schedule schedule : schedules) {
			schedule.setState(EScheduleState.NOT_RUNNING);
			session.update(schedule);
			Cycle last = getLastCycleForJob(schedule.getJob());
			if (last != null && last.getEndDateTime() == null) {
				last.setEndDateTime(new Date());
				session.update(last);
			}
		}
	}

	@Override
	public void createProcessedDocument(int cycleId, String name, Date date,
			String state, Set<ProcessedDocumentParameter> params) {
		logger.debug("Creating processed document:" + name);
		try {
			ProcessedDocument doc = new ProcessedDocument();
			doc.setCycle(getCycle(cycleId));
			doc.setName(name);
			doc.setProcessedDateTime(date);
			doc
					.setStatus(EProcessedDocumentStatus.valueOf(state
							.toUpperCase()));
			for(ProcessedDocumentParameter param : params) {
				if (param.getValue().length() > 255) {
					param.setValue(param.getValue().substring(0, 255));
				}
			}
			doc.setProcessedDocumentParameterSet(params);
			getSessionFactory().getCurrentSession().save(doc);
		} catch (Exception e) {
			logger.error("Failed to write " + name + " to report.", e);
		}
	}

	@Override
	public void sendMail(SimpleMailMessage message) {
		try {
			logger.debug("Sending email \"" + message.getSubject() + "\" to "
					+ Arrays.asList(message.getTo()));
			getMailSender().send(message);
		} catch (MailException e) {
			logger.warn("Failed to send email (" + e.getMessage() + ")");
		}
	}


	@Override
	public List<HistoryInfo> getHistory(int jobId) {
		List<HistoryInfo> historyList = new ArrayList<HistoryInfo>();
		Session s = getSessionFactory().getCurrentSession();
		List<Object[]> history = s
				.createSQLQuery(
						String
								.format(
										"select cycle.id as id, count(processedDocument.id) as count,"
												+ " cycle.startDateTime as startDateTime, schedule.state as state"
												+ " from cycle left join processedDocument on cycle.id=processedDocument.cycleId join schedule on schedule.id=cycle.scheduleId"
												+ " where schedule.jobId=%d group by cycle.id order by cycle.startDateTime desc;",
										jobId)).addScalar("id",
						StandardBasicTypes.INTEGER).addScalar("count",
						StandardBasicTypes.INTEGER).addScalar("startDateTime",
						StandardBasicTypes.TIMESTAMP).addScalar("state",
						StandardBasicTypes.STRING).list();
		for (Object[] cycle : history) {
			historyList.add(new HistoryInfo((Integer) cycle[0],
					(Date) cycle[2], (String) cycle[3], (Integer) cycle[1]));
		}

		return historyList;
	}

	/**
	 * Execute job with jobId as soon as possible
	 */
	@Override
	public void scheduleNow(int jobId, int scheduleId) {
		getScheduler().immediately(getJob(jobId), scheduleId);
	}
	
	
	@Override
	public int getDefaultScheduleIdForJob(int jobId) {
		int scheduleId;
		try{
			scheduleId = getScheduleId(jobId, SchedulerImpl.DEFAULT_SCHEDULE);
		} catch (IndexOutOfBoundsException e){ //first manual run
			scheduleId = createSchedule(jobId, SchedulerImpl.DEFAULT_SCHEDULE).getId();;	
		}
		return scheduleId;
	}

}
