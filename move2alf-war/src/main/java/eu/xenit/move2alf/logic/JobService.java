package eu.xenit.move2alf.logic;

import eu.xenit.move2alf.core.dto.*;
import eu.xenit.move2alf.core.enums.ECycleState;
import eu.xenit.move2alf.core.enums.EProcessedDocumentStatus;
import eu.xenit.move2alf.web.dto.HistoryInfo;
import eu.xenit.move2alf.web.dto.JobInfo;
import eu.xenit.move2alf.web.dto.JobModel;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Transactional
public interface JobService {

	/**
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('CONSUMER')")
	public List<Job> getAllJobs();

	/**
	 * Create a new job.
	 * 
	 * @param jobModel
	 *            The job configuration
	 * @return The new job
	 */
	@PreAuthorize("hasRole('JOB_ADMIN')")
	public Job createJob(JobModel jobModel);

	/**
	 * @param jobModel
	 *            The job configuration
	 * @return The edited job
	 */
	@PreAuthorize("hasRole('JOB_ADMIN')")
	public Job editJob(JobModel jobModel);

	/**
	 * Delete a job
	 * 
	 * @param id
	 *            The id of the job to delete
	 */
	@PreAuthorize("hasRole('JOB_ADMIN')")
	public void deleteJob(int id);

	/**
	 * Get a job with the given id.
	 * 
	 * @param id
	 *            The id of the job to get
	 * @return the job
	 */
	@PreAuthorize("hasRole('CONSUMER')")
	public Job getJob(int id);

	/**
	 * Return a cycle based on the cycle id
	 * 
	 * @param cycleId 		The id of the cycle
	 * return Cycle 		A cycles
	 */
	Cycle getCycle(int cycleId);
	
	/**
	 * Return all cycles for a job with the given name.
	 * 
	 * @param jobId
	 *            The id of the job return List<Cycle> A list of cycles
	 */
	@PreAuthorize("hasRole('CONSUMER')")
	public List<Cycle> getCyclesForJob(int jobId);

	/**
	 * Return all cycles for a job with the given name.
	 * 
	 * @param jobId
	 *            The id of the job return List<Cycle> A list of cycles
	 */
	List<Cycle> getCyclesForJobDesc(int jobId);
	
	/**
	 * Return the last cycle for each job.
	 * 
	 * @return List<Cycle> A list of cycles
	 */
	@PreAuthorize("hasRole('CONSUMER')")
	public Cycle getLastCycleForJob(Job job);

	/**
	 * Return all the schedules of a particular job
	 * 
	 * @param jobId
	 *            The id of the job
	 * @return a list of schedules
	 */
	@PreAuthorize("hasRole('CONSUMER')")
	public List<Schedule> getSchedulesForJob(int jobId);

	/**
	 * create a schedule
	 * 
	 * @param jobId
	 *            The id of the job
	 * @param cronJob
	 *            the cronjob as a String
	 * @return a list of schedules
	 */
	@PreAuthorize("hasRole('SCHEDULE_ADMIN')")
	public Schedule createSchedule(int jobId, String cronJob);

	/**
	 * Gets the cronjobs of a particular job
	 * 
	 * @param jobId
	 *            The id of the job
	 * @return a list of cronjobs
	 */
	@PreAuthorize("hasRole('CONSUMER')")
	public List<String> getCronjobsForJob(int jobId);

	/**
	 * Gets a schedule based on schedule id
	 * 
	 * @param scheduleId
	 *            The id of the schedule
	 * @return the schedule
	 */
	@PreAuthorize("hasRole('CONSUMER')")
	public Schedule getSchedule(int scheduleId);

	/**
	 * Deletes a schedule
	 * 
	 * @param scheduleId
	 *            The id of the schedule to be deleted
	 */
	@PreAuthorize("hasRole('SCHEDULE_ADMIN')")
	public void deleteSchedule(int scheduleId);

	/**
	 * Gets a schedule id based on job id and cronjob
	 * 
	 * @param jobId
	 *            The id of the schedule
	 * @param cronJob
	 *            The cronjob of the schedule
	 * @return the id
	 */
	@PreAuthorize("hasRole('CONSUMER')")
	public int getScheduleId(int jobId, String cronJob);
	
	/**
	 * 
	 * @param className
	 * @param parameters
	 */
	public void createSourceSink(String className, Map<String, String> parameters);

	/**
	 * 
	 * @param cycleId
	 */
	public List<ProcessedDocument> getProcessedDocuments(int cycleId);
	
	/**
	 * 
	 * @param cycleId
	 * @param first First ProcessedDocument to return (sorted by id). Starts at 0.
	 * @param count Number of ProcessedDocuments to return. 0 means return all.
	 */
	public List<ProcessedDocument> getProcessedDocuments(int cycleId, int first, int count);
	
	/**
	 * Return the number of ProcessedDocuments for a given cycle.
	 * 
	 * @param cycleId
	 */
	public long countProcessedDocuments(int cycleId);

	/**
	 * Return the number of ProcessedDocuments for a given cycle and status.
	 *
	 * @param cycleId
	 * @param status
	 */
	public long countProcessedDocumentsWithStatus(int cycleId, EProcessedDocumentStatus status);


	/**
	 * 
	 * @param jobName
	 * @return
	 */
	public boolean checkJobExists(String jobName);

	
	/**
	 * @param jobId
	 */
	public ECycleState getJobState(int jobId);
	
	/**
	 * Reset state of all schedules to NOT_RUNNING. 
	 */
	public void resetCycles();
	
	public void createProcessedDocument(int cycleId, String name, Date date, String state, Set<ProcessedDocumentParameter> params, String reference);

	public void sendMail(SimpleMailMessage message);

	public List<HistoryInfo> getHistory(int jobId);
	
	public List<JobInfo> getAllJobInfo();

	public void scheduleNow(int jobId);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void closeCycle(Cycle cycle);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    int openCycleForJob(Integer jobId);

    void startJob(Integer jobId);

    JobModel getJobConfigForJob(int id);

    int openCycleForJob(String jobId);

    void stopJob(int jobId);
}
