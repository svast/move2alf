package eu.xenit.move2alf.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSinkParameter;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.Schedule;
import eu.xenit.move2alf.core.dto.UserPswd;

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
	 * @param name 			The name of the job
	 * @param description 	The description of the job
	 * @return The new job
	 */
	@PreAuthorize("hasRole('JOB_ADMIN')")
	public Job createJob(String name, String description);

	/**
	 * Edit a job
	 * 
	 * @param id			The id of the job to edit
	 * @param name 			The name of the job
	 * @param description 	The description of the job
	 * @return The edited job
	 */
	@PreAuthorize("hasRole('JOB_ADMIN')")
	Job editJob(int id, String name, String description);
	
	
	/**
	 * Delete a job
	 * 
	 * @param id	The id of the job to delete
	 */
	@PreAuthorize("hasRole('JOB_ADMIN')")
	void deleteJob(int id);

	/**
	 * Get a job with the given id.
	 * 
	 * @param id		The id of the job to get
	 * @return the job
	 */
	@PreAuthorize("hasRole('CONSUMER')")
	Job getJob(int id);

	/**
	 * Return all cycles for a job with the given name.
	 * 
	 * @param jobId 	The id of the job
	 */
	@PreAuthorize("hasRole('CONSUMER')")
	public List<Cycle> getCyclesForJob(int jobId);

	
	/**
	 * Return all the schedules of a particular job
	 * 
	 * @param jobId	The id of the job
	 * @return a list of schedules
	 */
	@PreAuthorize("hasRole('CONSUMER')")
	List<Schedule> getSchedulesForJob(int jobId);

	/**
	 * create a schedule
	 * 
	 * @param jobId	The id of the job
	 * @param cronJob	the cronjob as a String
	 * @return a list of schedules
	 */
	@PreAuthorize("hasRole('SCHEDULE_ADMIN')")
	Schedule createSchedule(int jobId, String cronJob);

	/**
	 * Gets the cronjobs of a particular job
	 * 
	 * @param jobId	The id of the job
	 * @return a list of cronjobs
	 */
	@PreAuthorize("hasRole('CONSUMER')")
	List<String> getCronjobsForJob(int jobId);

	/**
	 * Gets a schedule based on schedule id
	 * 
	 * @param scheduleId	The id of the schedule
	 * @return the schedule
	 */
	@PreAuthorize("hasRole('CONSUMER')")
	Schedule getSchedule(int scheduleId);

	/**
	 * Deletes a schedule
	 * 
	 * @param scheduleId	The id of the schedule to be deleted
	 */
	@PreAuthorize("hasRole('SCHEDULE_ADMIN')")
	void deleteSchedule(int scheduleId);

	/**
	 * Gets a schedule id based on job id and cronjob
	 * 
	 * @param jobId	The id of the schedule
	 * @param cronJob 	The cronjob of the schedule
	 * @return the id
	 */
	@PreAuthorize("hasRole('CONSUMER')")
	int getScheduleId(int jobId, String cronJob);

	/**
	 * Creates a destination
	 * 
	 * @param destinationType		The type of destination
	 * @param destinationParams 	A map of parameters that define the destination
	 * @return the schedule
	 */
	@PreAuthorize("hasRole('JOB_ADMIN')")
	ConfiguredSourceSink createDestination(String destinationType, HashMap destinationParams);

	/**
	 * gets the configured source sinks based on the type of source sink (destination)
	 * 
	 * @param sourceSinkName		The type/classname of the source sink
	 * @return a list of configured source sinks
	 */
	@PreAuthorize("hasRole('CONSUMER')")
	List<ConfiguredSourceSink> getConfiguredSourceSink(String sourceSinkName);
	
}
