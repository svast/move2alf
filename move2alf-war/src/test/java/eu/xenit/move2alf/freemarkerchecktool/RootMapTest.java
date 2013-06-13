package eu.xenit.move2alf.freemarkerchecktool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.junit.*;

import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.Schedule;
import eu.xenit.move2alf.core.dto.UserPswd;
import eu.xenit.move2alf.core.dto.UserRole;

@Ignore
public class RootMapTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAdd() throws Exception {
		final RootMap rootMap = new RootMap();
		rootMap.add("job", Job.class);

		final Job job = (Job) rootMap.toMap().get("job");
		assertNotNull(job);
		assertEquals(1, job.getId());
		assertEquals("x", job.getName());
		assertEquals("x", job.getDescription());

		final UserPswd creator = job.getCreator();
		assertNotNull(creator);
		assertEquals(1, creator.getId());
		assertEquals("x", creator.getUserName());
		assertEquals("x", creator.getPassword());

		final Set<UserRole> userRoleSet = creator.getUserRoleSet();
		assertEquals(1, userRoleSet.size());
		final UserRole userRole = userRoleSet.iterator().next();
		assertNotNull(userRole);
		assertEquals("x", userRole.getUserName()); // TODO check why userName is
													// "" and not "x"
		// assertEquals(???, userRole.getRole()); //TODO setRole is not a
		// regular setter

		assertNotNull(job.getCreationDateTime()); // Date
		assertNotNull(job.getLastModifyDateTime()); // Date

		final ConfiguredAction configuredAction = job
				.getFirstConfiguredAction();
		assertNotNull(configuredAction);
//		final ConfiguredAction appliedConfiguredActionOnSuccess = configuredAction
//				.getAppliedConfiguredActionOnSuccess();
//		assertNotNull(appliedConfiguredActionOnSuccess); // TODO check fields of
//															// appliedConfiguredActionOnSuccess
//		final ConfiguredAction appliedConfiguredActionOnFailure = configuredAction
//				.getAppliedConfiguredActionOnFailure();
//		assertNotNull(appliedConfiguredActionOnFailure); // TODO check fields of
//															// appliedConfiguredActionOnFailure
//		final Set<ConfiguredSharedResource> configuredSourceSinkSet = configuredAction
//				.getConfiguredSourceSinkSet();
//		assertEquals(1, configuredSourceSinkSet.size()); // TODO check
//															// configuredSourceSinkSet

		final Set<Schedule> schedules = job.getSchedules();
		assertEquals(1, schedules.size()); // TODO check schedules

		final Set<Cycle> cycles = job.getCycles();
		assertEquals(1, cycles.size()); // TODO check cycles

//		final Set<Report> reports = job.getReports();
//		assertEquals(1, reports.size()); // TODO check reports
	}
}