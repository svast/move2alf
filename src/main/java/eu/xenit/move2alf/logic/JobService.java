package eu.xenit.move2alf.logic;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import eu.xenit.move2alf.core.dto.Job;

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

}
