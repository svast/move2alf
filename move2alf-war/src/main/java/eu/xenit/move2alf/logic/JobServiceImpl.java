package eu.xenit.move2alf.logic;

import akka.actor.ActorSystem;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.dto.*;
import eu.xenit.move2alf.core.enums.ECycleState;
import eu.xenit.move2alf.core.enums.EProcessedDocumentStatus;
import eu.xenit.move2alf.pipeline.JobHandle;
import eu.xenit.move2alf.pipeline.actions.ActionConfig;
import eu.xenit.move2alf.pipeline.actions.JobConfig;
import eu.xenit.move2alf.web.dto.HistoryInfo;
import eu.xenit.move2alf.web.dto.JobInfo;
import eu.xenit.move2alf.web.dto.JobModel;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;
import java.util.*;

@Service("jobService")
public class JobServiceImpl extends AbstractHibernateService implements
		JobService {
	private static final Logger logger = LoggerFactory
			.getLogger(JobServiceImpl.class);

    @Override
    public void stopJob(int jobId) {
        jobMap.get(jobId).destroy();
        jobMap.remove(jobId);
        closeCycle(getLastCycleForJob(getJob(jobId)));
    }

    @Override
    public Job getJobByName(String name) {
        final List<Job> jobs = getSessionFactory().getCurrentSession()
                .createQuery("from Job as j where j.name=?")
                .setString(0, name).list();
        return jobs.get(0);
    }

    private int getJobIdByName(String name) {
       return getJobByName(name).getId();
    }

    @Autowired
    private PipelineAssembler pipelineAssembler;

	private UserService userService;

    @Autowired
    private DestinationService destinationService;

	private Scheduler scheduler;

	private MailSender mailSender;

    @Autowired
    private ActorSystem actorSystem;

    private Map<Integer, JobHandle> jobMap = new HashMap<Integer, JobHandle>();

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
	public void setMailSender(final MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public MailSender getMailSender() {
		return mailSender;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Job> getAllJobs() {
		return getSessionFactory().getCurrentSession().createQuery("from Job")
				.list();
	}

	@Override
	// @Transactional(propagation=Propagation.REQUIRES_NEW)
	public Job createJob(JobModel jobModel) {

		final Job job = new Job();
        populateJobFields(jobModel, job);

        ConfiguredAction configuredAction =  pipelineAssembler.getConfiguredAction(jobModel);
        job.setFirstConfiguredAction(configuredAction);

        getSessionFactory().getCurrentSession().save(job);

		return job;
	}

    private void populateJobFields(JobModel jobModel, Job job) {
        final Date now = new Date();
        job.setName(jobModel.getName());
        job.setDescription(jobModel.getDescription());
        job.setCreationDateTime(now);
        job.setLastModifyDateTime(now);
        job.setCreator(getUserService().getCurrentUser());
        job.setSendErrorReport(jobModel.getSendNotification());
        job.setSendErrorReportTo(jobModel.getSendNotificationText());
        job.setSendReport(jobModel.getSendReport());
        job.setSendReportTo(jobModel.getSendReportText());
    }

    @Override
	public Job editJob(JobModel jobModel) {
		final Job job = getJob(jobModel.getId());

        populateJobFields(jobModel, job);

        ConfiguredAction oldConfiguredAction = job.getFirstConfiguredAction();
        job.setFirstConfiguredAction(pipelineAssembler.getConfiguredAction(jobModel));
        getSessionFactory().getCurrentSession().save(job);
        getSessionFactory().getCurrentSession().delete(oldConfiguredAction);
        if(jobMap.containsKey(jobModel.getId())){
            jobMap.get(jobModel.getId()).destroy();
            jobMap.remove(jobModel.getId());
        }
		return job;
	}

	@Override
	public void deleteJob(final int id) {
		final Job job = getJob(id);
        if(jobMap.get(id) != null){
            jobMap.get(id).destroy();
            jobMap.remove(id);
        }
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
	public void createSourceSink(final String className,
			final Map<String, String> parameters) {
		final ConfiguredSharedResource ss = new ConfiguredSharedResource();
		ss.setClassId(className);
		ss.setParameters(parameters);
		getSessionFactory().getCurrentSession().save(ss);
	}

	@Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ProcessedDocument> getProcessedDocuments(final int cycleId) {
		return getProcessedDocuments(cycleId, 0, 0);
	}

	@SuppressWarnings("unchecked")
	@Override
    @Transactional(propagation = Propagation.SUPPORTS)
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
	public ECycleState getJobState(final int jobId) {
		if(jobMap.get(jobId)!=null && jobMap.get(jobId).isRunning()){
            return ECycleState.RUNNING;
        }
        return ECycleState.NOT_RUNNING;
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
			final Set<ProcessedDocumentParameter> params, final String reference) {
		//logger.debug("Creating processed document:" + name);
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
            logger.debug("Number of params: "+params.size());
			doc.setProcessedDocumentParameterSet(params);
			doc.setReference(reference);
			getSessionFactory().getCurrentSession().save(doc);

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
				info.setNrOfDocuments(countProcessedDocuments(cycle.getId()));
				info.setNrOfFailedDocuments(countProcessedDocumentsWithStatus(cycle.getId(),
						EProcessedDocumentStatus.FAILED));
			}

			info.setScheduleState(getJobState(job.getId()).getDisplayName());

			info.setDescription(job.getDescription());
			jobInfoList.add(info);
		}

		return jobInfoList;
	}

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void closeCycle(final Cycle cycle) {
        final Session session = getSessionFactory().getCurrentSession();

        cycle.setEndDateTime(new Date());
        session.update(cycle);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int openCycleForJob(final Integer jobId) {
        final Session session = getSessionFactory().getCurrentSession();

        final Job job = getJob(jobId);
        logger.debug("Executing job \"" + job.getName() + "\"");

        final Cycle cycle = new Cycle();
        cycle.setJob(job);
        cycle.setStartDateTime(new Date());
        session.save(cycle);

        return cycle.getId();
    }

    private Map<Integer, RunnableWithCycle> onStopJobActions = new HashMap<Integer, RunnableWithCycle>();

    abstract class RunnableWithCycle implements Runnable {

        public Cycle cycle;

        @Override
        public abstract void run();
    }

    @Value(value="#{'${mail.from}'}")
    private String mailFrom;

    @Value(value="#{'${url}'}")
    private String url;

    @Autowired
    private ApplicationContext applicationContext;



    @Override
    public void startJob(Integer jobId) {
        if(!jobMap.containsKey(jobId)){
            final Job job = getJob(jobId);
            String name = job.getName();
            ConfiguredAction configuredAction = job.getFirstConfiguredAction();
            ActionConfig actionConfig = pipelineAssembler.getActionConfig(configuredAction);
            JobHandle jobHandle = new JobHandle(actorSystem, name, new JobConfig(actionConfig));

            RunnableWithCycle action = new RunnableWithCycle() {

                @Autowired
                JobService jobService;

                @Override
                @Transactional
                public void run() {

                    try {

                        jobService.closeCycle(cycle);

                        if(job.isSendReport() || job.isSendErrorReport()){
                            int cycleId = cycle.getId();

                            // only send report on errors
                            boolean errorsOccured = false;
                            int counter = 0;
                            int amountFailed = 0;
                            Date firstDocDateTime = null;
                            List<ProcessedDocument> processedDocuments = jobService.getProcessedDocuments(cycle.getId());
                            if (processedDocuments != null) {
                                for (ProcessedDocument doc : processedDocuments) {
                                    if (counter == 0) {
                                        firstDocDateTime = doc.getProcessedDateTime();
                                    }

                                    if (EProcessedDocumentStatus.FAILED.equals(doc.getStatus())) {
                                        errorsOccured = true;
                                        amountFailed += 1;
                                    }
                                    counter += 1;
                                }
                            }

                            // Get cycle information
                            long startDateTime = cycle.getStartDateTime().getTime();
                            long endDateTime = cycle.getEndDateTime().getTime();
                            long durationInSeconds = (endDateTime - startDateTime) / 1000;
                            String duration = Util.formatDuration(durationInSeconds);


                            List<String> addresses = new ArrayList<String>();
                            if(job.isSendReport() && job.getSendReportTo()!=null){
                                addresses.addAll(Arrays.asList(job.getSendReportTo().split(" *, *")));
                            }
                            if(job.isSendErrorReport() && errorsOccured && job.getSendErrorReportTo() != null){
                                addresses.addAll(Arrays.asList(job.getSendErrorReportTo().split(" *, *")));
                            }
							if(!addresses.isEmpty()){
								SimpleMailMessage mail = new SimpleMailMessage();
								mail.setFrom(mailFrom);
								mail.setTo(addresses.toArray(new String[addresses.size()]));
								mail.setSubject("Move2Alf error report");

								Job job = cycle.getJob();

								mail.setText("Cycle " + cycleId + " of job " + job.getName()
										+ " completed.\n" + "The full report can be found on "
										+ url + "/job/" + job.getId() + "/" + cycleId
										+ "/report" + "\n\nStatistics:" + "\nNr of files: "
										+ processedDocuments.size() + "\nNr of failed: "
										+ amountFailed + "\n\nTime to process: " + duration
										+ "\nStart date/time: " + startDateTime
										+ "\nTime first document loaded: " + firstDocDateTime
										+ "\n\nSent by Move2Alf");

								sendMail(mail);
							} else {
								logger.debug("Not sending email, because no email addresses to send to");
							}

                        }


                    } catch (Exception e) {
                        logger.error("Error", e);
                    }
                }
            };

            applicationContext.getAutowireCapableBeanFactory().autowireBean(action);
            jobHandle.registerOnStopAction(action);
            onStopJobActions.put(jobId, action);
            jobMap.put(job.getId(), jobHandle);
        }

        JobHandle handle = jobMap.get(jobId);
        if(!handle.isRunning()){
            int cycleId = openCycleForJob(jobId);
            onStopJobActions.get(jobId).cycle = getCycle(cycleId);
            jobMap.get(jobId).startJob();
        } else {
            logger.warn("Job " + handle.id() + " is already running. It cannot be started again");
        }
    }

    @Override
    public JobModel getJobConfigForJob(int id) {
        return pipelineAssembler.getJobConfigForJob(id);
    }

    @PreDestroy
    public void preDestroy(){
        logger.debug("Stopping the actorSystem");
        for(JobHandle handle: jobMap.values()){
            handle.destroy();
        }
        actorSystem.shutdown();
        actorSystem.awaitTermination();
    }

}
