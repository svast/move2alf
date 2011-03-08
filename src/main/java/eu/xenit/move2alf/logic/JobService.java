package eu.xenit.move2alf.logic;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import eu.xenit.move2alf.core.ConfiguredObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.ProcessedDocument;
import eu.xenit.move2alf.core.dto.Schedule;

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
	 * @param name
	 *            The name of the job
	 * @param description
	 *            The description of the job
	 * @return The new job
	 */
	@PreAuthorize("hasRole('JOB_ADMIN')")
	public Job createJob(String name, String description);

	/**
	 * Edit a job
	 * 
	 * @param id
	 *            The id of the job to edit
	 * @param name
	 *            The name of the job
	 * @param description
	 *            The description of the job
	 * @return The edited job
	 */
	@PreAuthorize("hasRole('JOB_ADMIN')")
	public Job editJob(int id, String name, String description);

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
	 * Creates a destination
	 * 
	 * @param destinationType
	 *            The type of destination
	 * @param destinationParams
	 *            A map of parameters that define the destination
	 * @return the configured source sink
	 */
	@PreAuthorize("hasRole('JOB_ADMIN')")
	public ConfiguredSourceSink createDestination(String destinationType,
			HashMap destinationParams);

	/**
	 * Edits a destination
	 * 
	 * @param dinkId
	 * @param destinationType
	 *            The type of destination
	 * @param destinationParams
	 *            A map of parameters that define the destination
	 * @return the configured source sink
	 */
	public ConfiguredSourceSink editDestination(int sinkId, String destinationType,
			HashMap destinationParams);
	
	/**
	 * Get ConfiguredSourceSink by id.
	 * 
	 * @param sinkId
	 * @return
	 */
	public ConfiguredSourceSink getDestination(int sinkId);

	/**
	 * gets all configured source sinks
	 * 
	 * @return a list of configured source sinks
	 */
	@PreAuthorize("hasRole('CONSUMER')")
	public List<ConfiguredSourceSink> getAllConfiguredSourceSinks();

	/**
	 * gets a configured source sink based on id.
	 * 
	 * @param sourceSinkId
	 * @return a list of configured source sinks
	 */
	public ConfiguredObject getConfiguredSourceSink(int sourceSinkId);

	/**
	 * gets the duration of the cycle based on startDateTime and EndDateTime
	 * 
	 * @param startDateTime
	 * @param endDateTime
	 * @return String
	 */
	public String getDuration(Date startDateTime, Date endDateTime);
	
	/**
	 * Start execution of a job
	 * 
	 * @param scheduleId
	 *            The id of the schedule to start
	 */
	@PreAuthorize("hasRole('SCHEDULE_ADMIN')")
	public void executeJob(int scheduleId);

	/**
	 * deletes a configured source sink based on id.
	 * 
	 * @param id
	 */
	public void deleteDestination(int id);

	/**
	 * Start a new cycle for a job. This is the first step when executing a job.
	 * 
	 * @param scheduleId The id of the schedule that's starting the job.
	 * @return The new cycle
	 */
	public Cycle openCycleForSchedule(Integer scheduleId);

	/**
	 * Stop/close a cycle after executing the job.
	 * 
	 * @param cycleId The cycle to close
	 */
	public void closeCycle(Cycle cycle);
	
	/**
	 * Execute a configured action.
	 * 
	 * @param action
	 * @param parameterMap
	 */
	public void executeAction(ConfiguredAction action, Map<String, Object> parameterMap);
	
	/**
	 * 
	 * @param className
	 * @param parameters
	 */
	public void createAction(String className, Map<String, String> parameters);
	
	/**
	 * 
	 * @param className
	 * @param parameters
	 */
	public void createSourceSink(String className, Map<String, String> parameters);
	
	/**
	 * 
	 * @param action
	 * @param sourceSink
	 */
	public void addSourceSinkToAction(ConfiguredAction action, ConfiguredSourceSink sourceSink);
	
	/**
	 * 
	 * @param action
	 * @param nextAction
	 */
	public void setNextAction(ConfiguredAction action, ConfiguredAction nextAction);

	/**
	 * 
	 * @param cycleId
	 */
	public List<ProcessedDocument> getProcessedDocuments(int cycleId);
}
