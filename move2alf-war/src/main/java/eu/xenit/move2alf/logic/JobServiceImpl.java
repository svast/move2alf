package eu.xenit.move2alf.logic;

import static akka.actor.Actors.actorOf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import akka.actor.ActorRef;
import eu.xenit.move2alf.common.IdObject;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.ConfiguredObject;
import eu.xenit.move2alf.core.ReportActor;
import eu.xenit.move2alf.core.action.Action;
import eu.xenit.move2alf.core.action.ActionFactory;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.ProcessedDocument;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.core.dto.Schedule;
import eu.xenit.move2alf.core.enums.ECycleState;
import eu.xenit.move2alf.core.enums.EDestinationParameter;
import eu.xenit.move2alf.core.enums.EProcessedDocumentStatus;
import eu.xenit.move2alf.core.sourcesink.SourceSink;
import eu.xenit.move2alf.core.sourcesink.SourceSinkFactory;
import eu.xenit.move2alf.logic.usageservice.UsageService;
import eu.xenit.move2alf.web.dto.HistoryInfo;
import eu.xenit.move2alf.web.dto.JobInfo;

@Service("jobService")
public class JobServiceImpl extends AbstractHibernateService implements
		JobService {
	private static final Logger logger = LoggerFactory
			.getLogger(JobServiceImpl.class);
	
	@Autowired
	private UsageService usageService;

	private UserService userService;

	private Scheduler scheduler;

	private ActionFactory actionFactory;

	private SourceSinkFactory sourceSinkFactory;

	private MailSender mailSender;

	private final ActorRef reportActor;

	@Autowired
	public void setUserService(final UserService userService) {
		this.userService = userService;
	}

	public UserService getUserService() {
		return userService;
	}

	@Autowired
	public void setScheduler(final Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	@Autowired
	public void setActionFactory(final ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
	}

	public ActionFactory getActionFactory() {
		return actionFactory;
	}

	@Autowired
	public void setSourceSinkFactory(final SourceSinkFactory sourceSinkFactory) {
		this.sourceSinkFactory = sourceSinkFactory;
	}

	public SourceSinkFactory getSourceSinkFactory() {
		return sourceSinkFactory;
	}

	@Autowired
	public void setMailSender(final MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public MailSender getMailSender() {
		return mailSender;
	}

	@Override
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
	// @Transactional(propagation=Propagation.REQUIRES_NEW)
	public Job createJob(final String name, final String description) {
		final Date now = new Date();
		final Job job = new Job();
		job.setName(name);
		job.setDescription(description);
		job.setCreationDateTime(now);
		job.setLastModifyDateTime(now);
		job.setCreator(getUserService().getCurrentUser());
		getSessionFactory().getCurrentSession().save(job);
		return job;
	}

	@Override
	public Job editJob(final int id, final String name, final String description) {
		final Date now = new Date();
		final Job job = getJob(id);
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
	public void deleteJob(final int id) {
		final Job job = getJob(id);
		sessionFactory.getCurrentSession().delete(job);
		logger.debug("Reloading scheduler");
		getSessionFactory().getCurrentSession().flush();
		getScheduler().reloadSchedules();
	}

	@Override
	public Job getJob(final int id) {
		@SuppressWarnings("unchecked")
		final List<Job> jobs = sessionFactory.getCurrentSession()
				.createQuery("from Job as j where j.id=?").setLong(0, id)
				.list();
		if (jobs.size() == 1) {
			return jobs.get(0);
		} else {
			throw new Move2AlfException("Job with id " + id + " not found.");
		}
	}

	@Override
	public boolean checkJobExists(final String jobName) {
		@SuppressWarnings("unchecked")
		final List<Job> jobs = sessionFactory.getCurrentSession()
				.createQuery("from Job as j where j.name=?")
				.setString(0, jobName).list();
		return (jobs.size() > 0);
	}

	@Override
	public Cycle getCycle(final int cycleId) {
		@SuppressWarnings("unchecked")
		final List<Cycle> cycles = getSessionFactory().getCurrentSession()
				.createQuery("from Cycle as c where c.id=?")
				.setLong(0, cycleId).list();
		return cycles.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Cycle> getCyclesForJob(final int jobId) {
		return getSessionFactory()
				.getCurrentSession()
				.createQuery(
						"from Cycle as c where c.job.id=? order by c.endDateTime asc")
				.setLong(0, jobId).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Cycle> getCyclesForJobDesc(final int jobId) {
		return getSessionFactory()
				.getCurrentSession()
				.createQuery(
						"from Cycle as c where c.job.id=? order by c.startDateTime desc")
				.setLong(0, jobId).list();
	}

	@Override
	public Cycle getLastCycleForJob(final Job job) {

		@SuppressWarnings("unchecked")
		final List<Cycle> list = getSessionFactory()
				.getCurrentSession()
				.createQuery(
						"from Cycle as c where c.job.id=? order by c.startDateTime desc limit 1")
				.setLong(0, job.getId()).list();

		if (list.isEmpty()) {
			return null;
		}

		return list.get(0);
	}

	@Override
	public List<Schedule> getSchedulesForJob(final int jobId) {
		@SuppressWarnings("unchecked")
		final List<Schedule> schedules = sessionFactory.getCurrentSession()
				.createQuery("from Schedule as s where s.job.id=?")
				.setLong(0, jobId).list();
		return schedules;
	}

	@Override
	public Schedule getSchedule(final int scheduleId) {
		@SuppressWarnings("unchecked")
		final List<Schedule> schedules = sessionFactory.getCurrentSession()
				.createQuery("from Schedule as s where s.id=?")
				.setLong(0, scheduleId).list();
		if (schedules.size() == 1) {
			return schedules.get(0);
		} else {
			throw new Move2AlfException("Schedule with id " + scheduleId
					+ " not found.");
		}
	}

	@Override
	@Transactional(noRollbackFor = IndexOutOfBoundsException.class)
	public int getScheduleId(final int jobId, final String cronJob) {
		@SuppressWarnings("unchecked")
		final List<Schedule> schedule = sessionFactory
				.getCurrentSession()
				.createQuery(
						"from Schedule as s where s.job.id=? and s.quartzScheduling=?")
				.setLong(0, jobId).setString(1, cronJob).list();

		return schedule.get(0).getId();
	}

	@Override
	public List<String> getCronjobsForJob(final int jobId) {
		final List<Schedule> schedules = getSchedulesForJob(jobId);
		final List<String> cronjobs = new ArrayList<String>();

		for (int i = 0; i < schedules.size(); i++) {
			cronjobs.add(schedules.get(i).getQuartzScheduling());
		}
		return cronjobs;
	}

	@Override
	public Schedule createSchedule(final int jobId, final String cronJob) {
		final Date now = new Date();
		final Schedule schedule = new Schedule();
		final Job job = getJob(jobId);
		schedule.setJob(job);
		schedule.setCreator(getUserService().getCurrentUser());
		schedule.setCreationDateTime(now);
		schedule.setLastModifyDateTime(now);
		schedule.setQuartzScheduling(cronJob);
		getSessionFactory().getCurrentSession().save(schedule);

		logger.debug("Reloading scheduler");
		getSessionFactory().getCurrentSession().evict(job);
		// job object is still in cache with old schedules
		getScheduler().reloadSchedules();

		return schedule;
	}

	@Override
	public void deleteSchedule(final int scheduleId) {
		final Schedule schedule = getSchedule(scheduleId);
		final Job job = schedule.getJob();
		sessionFactory.getCurrentSession().delete(schedule);

		logger.debug("Reloading scheduler");
		getSessionFactory().getCurrentSession().evict(job);
		// job object is still in cache with old schedules
		getSessionFactory().getCurrentSession().flush();
		getScheduler().reloadSchedules();
	}

	@Override
	public ConfiguredSourceSink createDestination(final String destinationType,
			final HashMap<EDestinationParameter, Object> destinationParams) {
		final ConfiguredSourceSink sourceSink = new ConfiguredSourceSink();
		createSourceSink(destinationType, destinationParams, sourceSink);
		getSessionFactory().getCurrentSession().save(sourceSink);
		return sourceSink;
	}

	@Override
	public ConfiguredSourceSink editDestination(final int sinkId,
			final String destinationType,
			final HashMap<EDestinationParameter, Object> destinationParams) {
		final ConfiguredSourceSink sourceSink = getConfiguredSourceSink(sinkId);
		sourceSink.setClassName(destinationType);
		createSourceSink(destinationType, destinationParams, sourceSink);
		getSessionFactory().getCurrentSession().save(sourceSink);
		return sourceSink;
	}

	@Override
	public ConfiguredSourceSink getDestination(final int id) {
		return (ConfiguredSourceSink) getSessionFactory().getCurrentSession()
				.get(ConfiguredSourceSink.class, id);
	}

	@Override
	public boolean checkDestinationExists(final String destinationName) {
		@SuppressWarnings("unchecked")
		final List<ConfiguredObject> destinations = sessionFactory
				.getCurrentSession().createQuery("from ConfiguredSourceSink")
				.list();

		for (int i = 0; i < destinations.size(); i++) {
			final String destinationParameter = destinations.get(i)
					.getParameter("name");

			if (destinationName.equals(destinationParameter)) {
				return true;
			}
		}
		return false;
	}

	private void createSourceSink(final String destinationType,
			final HashMap<EDestinationParameter, Object> destinationParams,
			final ConfiguredSourceSink sourceSink) {
		sourceSink.setClassName(destinationType);

		final Map<String, String> sourceSinkParameters = new HashMap<String, String>();
		sourceSinkParameters.put("name",
				(String) destinationParams.get(EDestinationParameter.NAME));
		sourceSinkParameters.put("url",
				(String) destinationParams.get(EDestinationParameter.URL));
		sourceSinkParameters.put("user",
				(String) destinationParams.get(EDestinationParameter.USER));
		sourceSinkParameters.put("password",
				(String) destinationParams.get(EDestinationParameter.PASSWORD));
		sourceSinkParameters
				.put("threads",
						destinationParams.get(EDestinationParameter.THREADS)
								.toString());
		sourceSink.setParameters(sourceSinkParameters);
	}

	@Override
	public List<ConfiguredSourceSink> getAllConfiguredSourceSinks() {
		@SuppressWarnings("unchecked")
		final List<ConfiguredSourceSink> configuredSourceSink = sessionFactory
				.getCurrentSession().createQuery("from ConfiguredSourceSink")
				.list();

		return configuredSourceSink;
	}

	@Override
	public List<ConfiguredSourceSink> getAllDestinationConfiguredSourceSinks() {

		final String fileSourceSink = "eu.xenit.move2alf.core.sourcesink.FileSourceSink";
		@SuppressWarnings("unchecked")
		final List<ConfiguredSourceSink> configuredSourceSink = sessionFactory
				.getCurrentSession()
				.createQuery(
						"from ConfiguredSourceSink as c where c.className!=?")
				.setString(0, fileSourceSink).list();

		return configuredSourceSink;
	}

	@Override
	public ConfiguredSourceSink getConfiguredSourceSink(final int sourceSinkId) {
		@SuppressWarnings("unchecked")
		final List<ConfiguredSourceSink> configuredSourceSink = sessionFactory
				.getCurrentSession()
				.createQuery("from ConfiguredSourceSink as c where c.id=?")
				.setLong(0, sourceSinkId).list();
		if (configuredSourceSink.size() == 1) {
			return configuredSourceSink.get(0);
		} else {
			throw new Move2AlfException("ConfiguredSourceSink with id "
					+ sourceSinkId + " not found");
		}
	}

	@Override
	public ConfiguredAction getActionRelatedToConfiguredSourceSink(
			final int sourceSinkId) {
		@SuppressWarnings("unchecked")
		final List<ConfiguredAction> configuredActions = sessionFactory
				.getCurrentSession().createQuery("from ConfiguredAction")
				.list();

		for (int i = 0; i < configuredActions.size(); i++) {
			final Set<ConfiguredSourceSink> configuredSourceSinkForAction = configuredActions
					.get(i).getConfiguredSourceSinkSet();

			for (int j = 0; j < configuredSourceSinkForAction.size(); j++) {
				final Iterator<ConfiguredSourceSink> configuredSourceSinkIterator = configuredSourceSinkForAction
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
	public void deleteDestination(final int id) {
		final ConfiguredSourceSink destination = getConfiguredSourceSink(id);
		final Map<String, String> emptyMap = new HashMap<String, String>();
		destination.setParameters(emptyMap);
		sessionFactory.getCurrentSession().delete(destination);
	}

	@Override
	public void executeAction(final int cycleId, final ConfiguredAction action,
			final Map<String, Object> parameterMap) {
		throw new UnsupportedOperationException(
				"Use JobExecutionService instead");
	}

	@Override
	public Map<String, String> getActionParameters(final int cycleId,
			final Class<? extends Action> clazz) {
		final Cycle cycle = getCycle(cycleId);
		ConfiguredAction action = cycle.getJob().getFirstConfiguredAction();
		while (action != null) {
			if (clazz.getName().equals(action.getClassName())) {
				return action.getParameters();
			}
			action = action.getAppliedConfiguredActionOnSuccess();
		}
		return null;
	}

	@Override
	public void addSourceSinkToAction(final ConfiguredAction action,
			final ConfiguredSourceSink sourceSink) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createAction(final String className,
			final Map<String, String> parameters) {
		final ConfiguredAction action = new ConfiguredAction();
		action.setClassName(className);
		action.setParameters(parameters);
		getSessionFactory().getCurrentSession().save(action);
	}

	@Override
	public void createSourceSink(final String className,
			final Map<String, String> parameters) {
		final ConfiguredSourceSink ss = new ConfiguredSourceSink();
		ss.setClassName(className);
		ss.setParameters(parameters);
		getSessionFactory().getCurrentSession().save(ss);
	}

	@Override
	public List<ProcessedDocument> getProcessedDocuments(final int cycleId) {
		return getProcessedDocuments(cycleId, 0, 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProcessedDocument> getProcessedDocuments(final int cycleId,
			final int first, final int count) {
		final Query query = sessionFactory.getCurrentSession()
				.createQuery("from ProcessedDocument as d where d.cycle.id=?")
				.setLong(0, cycleId).setFirstResult(first);
		if (count > 0) {
			query.setMaxResults(count);
		}

		return query.list();
	}

	@Override
	public long countProcessedDocuments(final int cycleId) {
		final Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"select count(*) from ProcessedDocument as d where d.cycle.id=?")
				.setLong(0, cycleId);
		return (Long) query.uniqueResult();
	}

	@Override
	public long countProcessedDocumentsWithStatus(final int cycleId, EProcessedDocumentStatus status) {
		final Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"select count(*) from ProcessedDocument as d where d.cycle.id=? and d.status=?")
				.setLong(0, cycleId)
				.setParameter(1, status);
		return (Long) query.uniqueResult();
	}

	@Override
	public void deleteAction(final int id) {
		final Session s = getSessionFactory().getCurrentSession();
		s.delete(s.get(ConfiguredAction.class, id));
	}

	@Override
	public List<Action> getActionsByCategory(final String category) {
		return getActionFactory().getObjectsByCategory(category);
	}

	@Override
	public List<SourceSink> getSourceSinksByCategory(final String category) {
		return getSourceSinkFactory().getObjectsByCategory(category);
	}

	@Override
	public ECycleState getJobState(final int jobId) {
		logger.debug("Getting state of job: " + jobId);
		final String hql = "SELECT cycle.id FROM Cycle as cycle WHERE cycle.job.id= :jobId AND cycle.endDateTime is null";
		final Session s = sessionFactory.getCurrentSession();
		final Query q = s.createQuery(hql);
		q.setParameter("jobId", jobId);
		if (q.list().size() == 0) {
			return ECycleState.NOT_RUNNING;
		} else {
			return ECycleState.RUNNING;
		}
	}

	@Override
	public void resetCycles() {
		logger.debug("Resetting cycles");
		final String hql = "UPDATE Cycle as cycle SET cycle.endDateTime = :now WHERE cycle.endDateTime is null";
		final Session session = getSessionFactory().getCurrentSession();
		final Query q = session.createQuery(hql);
		q.setTimestamp("now", new Date());
		q.executeUpdate();
	}

	@Override
	public void createProcessedDocument(final int cycleId, final String name,
			final Date date, final String state,
			final Set<ProcessedDocumentParameter> params) {
		logger.debug("Creating processed document:" + name);
		try {
			final ProcessedDocument doc = new ProcessedDocument();
			doc.setCycle(getCycle(cycleId));
			doc.setName(name);
			doc.setProcessedDateTime(date);
			doc.setStatus(EProcessedDocumentStatus.valueOf(state.toUpperCase()));
			for (final ProcessedDocumentParameter param : params) {
				if (param.getValue().length() > 255) {
					param.setValue(param.getValue().substring(0, 255));
				}
			}
			doc.setProcessedDocumentParameterSet(params);
			getSessionFactory().getCurrentSession().save(doc);
			if ( EProcessedDocumentStatus.OK.equals(doc.getStatus()) ) {
				usageService.decrementDocumentCounter();
			}
		} catch (final Exception e) {
			logger.error("Failed to write " + name + " to report.", e);
		}
	}

	@Override
	public void sendMail(final SimpleMailMessage message) {
		try {
			logger.debug("Sending email \"" + message.getSubject() + "\" to "
					+ Arrays.asList(message.getTo()));
			getMailSender().send(message);
		} catch (final MailException e) {
			logger.warn("Failed to send email (" + e.getMessage() + ")");
		}
	}

	@Override
	public List<HistoryInfo> getHistory(final int jobId) {
		final List<HistoryInfo> historyList = new ArrayList<HistoryInfo>();
		final Session s = getSessionFactory().getCurrentSession();

		final String hql = "SELECT cycle.id, COUNT(processedDocument), cycle.startDateTime, cycle.endDateTime FROM Cycle AS cycle LEFT JOIN cycle.processedDocuments AS processedDocument WHERE cycle.job.id=:jobId GROUP BY cycle.id ORDER BY cycle.startDateTime DESC";
		final Query query = s.createQuery(hql);
		query.setParameter("jobId", jobId);

		@SuppressWarnings("unchecked")
		final List<Object[]> history = query.list();

		for (final Object[] cycle : history) {
			final HistoryInfo info = new HistoryInfo();
			info.setCycleId((Integer) cycle[0]);
			info.setNbrOfDocuments(((Long) cycle[1]).intValue());
			info.setCycleStartDateTime((Date) cycle[2]);
			if (cycle[3] == null) {
				info.setScheduleState(ECycleState.RUNNING.getDisplayName());
			} else {
				info.setScheduleState(ECycleState.NOT_RUNNING.getDisplayName());
			}
			historyList.add(info);
		}

		return historyList;
	}

	/**
	 * Execute job with jobId as soon as possible
	 */
	@Override
	public void scheduleNow(final int jobId) {
		getScheduler().immediately(getJob(jobId));
	}

	@Override
	public List<JobInfo> getAllJobInfo() {
		final List<JobInfo> jobInfoList = new ArrayList<JobInfo>();
		final Session s = getSessionFactory().getCurrentSession();
		final String hql = "FROM Job AS job ORDER BY job.id ASC";

		@SuppressWarnings("unchecked")
		final List<Job> jobs = s.createQuery(hql).list();

		for (final Job job : jobs) {
			final JobInfo info = new JobInfo();
			info.setJobId(job.getId());
			info.setJobName(job.getName());
			final Cycle cycle = getLastCycleForJob(job);
			if (cycle != null) {
				info.setCycleStartDateTime(cycle.getStartDateTime());
				info.setScheduleState(cycle.getState().getDisplayName());
				info.setNrOfDocuments(countProcessedDocuments(cycle.getId()));
				info.setNrOfFailedDocuments(countProcessedDocumentsWithStatus(cycle.getId(),
						EProcessedDocumentStatus.FAILED));
			} else {
				info.setScheduleState(ECycleState.NOT_RUNNING.getDisplayName());
			}
			info.setDescription(job.getDescription());
			jobInfoList.add(info);
		}

		return jobInfoList;
	}

}
