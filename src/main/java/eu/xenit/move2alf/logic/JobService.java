package eu.xenit.move2alf.logic;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
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
	 * Delete a job
	 * 
	 * @param id	The id of the job to delete
	 */
	@PreAuthorize("hasRole('JOB_ADMIN')")
	void deleteJob(int id);

	@PreAuthorize("hasRole('CONSUMER')")
	Job getJob(int id);

	@PreAuthorize("hasRole('JOB_ADMIN')")
	Job editJob(int id, String name, String description);

	/**
	 * Return all cycles for a job with the given name.
	 * 
	 * @param jobName The name of the job
	 */
	@PreAuthorize("hasRole('CONSUMER')")
	public List<Cycle> getCyclesForJob(String jobName);
}
