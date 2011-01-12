package eu.xenit.move2alf;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.logic.JobService;

public class JobsTests extends IntegrationTests {

	private JobService jobService;

	@Autowired
	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}

	public JobService getJobService() {
		return jobService;
	}
	
	@Test
	public void testCreateJob() {
		loginAsAdmin();
		Date before = new Date();
		Job newJob = getJobService().createJob("test job", "description of test job");
		assertEquals("test job", newJob.getName());
		assertEquals("description of test job", newJob.getDescription());
		Date after = new Date();
		assertTrue(before.before(newJob.getCreationDateTime()));
		assertTrue(after.after(newJob.getCreationDateTime()));
		assertTrue(before.before(newJob.getLastModifyDateTime()));
		assertTrue(after.after(newJob.getLastModifyDateTime()));
		assertEquals("admin", newJob.getCreator().getUserName());
	}
	
	@Test
	public void testGetAllJobs() {
		getJobService().createJob("testJob", "description of test job");
		List<Job> jobs = getJobService().getAllJobs();
		assertNotNull(jobs);
		boolean testJobInResult = false;
		for (Job job : jobs) {
			if ("testJob".equals(job.getName())) {
				testJobInResult = true;
				break;
			}
		}
		assertTrue(testJobInResult);
	}
	
	@Test
	public void testGetCyclesForJob() {
		getJobService().createJob("testJob", "description of test job");
		List<Cycle> cycles = getJobService().getCyclesForJob("testJob");
		assertNotNull(cycles);
		// TODO: run job, see if cycle is created
	}
}
