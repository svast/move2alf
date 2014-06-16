package eu.xenit.move2alf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.logic.JobService;
import eu.xenit.move2alf.logic.Scheduler;

public class JobsTests extends IntegrationTests {

	private static final Logger logger = LoggerFactory
			.getLogger(JobsTests.class);

	private JobService jobService;

	@Autowired
	public void setJobService(final JobService jobService) {
		this.jobService = jobService;
	}

	public JobService getJobService() {
		return jobService;
	}

	private Scheduler scheduler;

	@Autowired
	public void setScheduler(final Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public Scheduler getScheduler() {
		return this.scheduler;
	}

//	@Test
//	public void testCreateJob() {
//		loginAsAdmin();
//		final Date before = new Date();
//		final Job newJob = getJobService().createJob("test job",
//				"description of test job");
//		assertEquals("test job", newJob.getName());
//		assertEquals("description of test job", newJob.getDescription());
//		final Date after = new Date();
//		assertTrue(before.before(newJob.getCreationDateTime())
//				|| before.equals(newJob.getCreationDateTime()));
//		assertTrue(after.after(newJob.getCreationDateTime())
//				|| after.equals(newJob.getCreationDateTime()));
//		assertTrue(before.before(newJob.getLastModifyDateTime())
//				|| before.equals(newJob.getLastModifyDateTime()));
//		assertTrue(after.after(newJob.getLastModifyDateTime())
//				|| after.equals(newJob.getLastModifyDateTime()));
//		assertEquals("admin", newJob.getCreator().getUserName());
//		getJobService().destroy(newJob.getId());
//	}
//
//	@Test
//	public void testGetAllJobs() {
//		final Job newJob = getJobService().createJob("testJob",
//				"description of test job");
//		final List<Job> jobs = getJobService().getAllJobs();
//		assertNotNull(jobs);
//		boolean testJobInResult = false;
//		for (final Job job : jobs) {
//			if ("testJob".equals(job.getName())) {
//				testJobInResult = true;
//				break;
//			}
//		}
//		assertTrue(testJobInResult);
//		getJobService().destroy(newJob.getId());
//	}
//
//	@Test
//	public void testGetCyclesForJob() throws InterruptedException {
//		final Job newJob = getJobService().createJob("testJob2",
//				"description of test job");
//
//		final List<Cycle> cycles = getJobService().getCyclesForJob(
//				newJob.getId());
//		assertNotNull(cycles);
//		assertEquals(0, cycles.size());
//
//		getScheduler().immediately(newJob);
//		// cycles = getJobService().getCyclesForJob(newJob.getId());
//		Thread.sleep(1000);
//
//		getJobService().destroy(newJob.getId());
//		// assertEquals(1, cycles.size());
//		// TODO: run job, see if cycle is created
//		// TODO: do cycles need to be sorted chronologically?
//	}

	// get summary from latest cycle

	// get status of job (running, not running (last run), disabled, ...)

}
