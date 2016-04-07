package eu.xenit.move2alf.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import eu.xenit.move2alf.BaseIntegrationTest;
import eu.xenit.move2alf.TransactionalIntegrationTests;
import eu.xenit.move2alf.core.dto.*;
import eu.xenit.move2alf.core.enums.ECycleState;
import eu.xenit.move2alf.core.sharedresource.SharedResourceService;
import eu.xenit.move2alf.logic.DestinationService;
import eu.xenit.move2alf.web.controller.destination.AlfrescoHttpDestinationTypeController;
import eu.xenit.move2alf.web.controller.destination.model.AlfrescoDestinationModel;
import eu.xenit.move2alf.web.dto.JobModel;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eu.xenit.move2alf.logic.JobService;
import eu.xenit.move2alf.logic.Scheduler;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;

/**
 * Warning, notice that the db is not cleared between subsequent calls
 */
//@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // This restarts the spring context for every test
public class JobsTests extends BaseIntegrationTest {

    //@Autowired
    AlfrescoHttpDestinationTypeController ctrl;
    @Autowired
    DestinationService destinationService;

    @Autowired
    ApplicationContext ctx;

    private static final Logger logger = LoggerFactory
            .getLogger(JobsTests.class);

    private JobService jobService;
    @Autowired
    private SharedResourceService sharedResourceService;

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


    @Before
    public void Setup() {
        ctrl = new AlfrescoHttpDestinationTypeController();
        ctx.getAutowireCapableBeanFactory().autowireBean(ctrl);
        loginAsAdmin();


        reset();
    }


    private void reset() {
        for (Resource d : destinationService.getAllDestinations())
            ctrl.deleteDestination(d.getId());
        //destinationService.deleteDestination(d);
    }

    @Test
    public void testCreateJob() {

        final Date before = new Date();

        JobModel jobModel = JobModel.CreateDefault("test create job");

        final Job newJob = getJobService().createJob(jobModel);
        assertEquals("test create job", newJob.getName());
        assertEquals("Description of test create job", newJob.getDescription());
        final Date after = new Date();
        assertTrue(before.before(newJob.getCreationDateTime())
                || before.equals(newJob.getCreationDateTime()));
        assertTrue(after.after(newJob.getCreationDateTime())
                || after.equals(newJob.getCreationDateTime()));
        assertTrue(before.before(newJob.getLastModifyDateTime())
                || before.equals(newJob.getLastModifyDateTime()));
        assertTrue(after.after(newJob.getLastModifyDateTime())
                || after.equals(newJob.getLastModifyDateTime()));
        assertEquals("admin", newJob.getCreator().getUserName());
        getJobService().deleteJob(newJob.getId());
    }

    @Test
    public void testCreateDestination() {
//        for (String n : ctx.getBeanDefinitionNames())
//            System.out.println(n);


        //createAlfrescoDestination();
        //createMockedAlfrescoDestination();
        for (Resource dest : destinationService.getAllDestinations()) {
            System.out.println(dest);
        }
    }

    @Test
    public void testCreateMockedDestination() {
        //createAlfrescoDestination();
        createMockedAlfrescoDestination();
        for (Resource dest : destinationService.getAllDestinations()) {
            System.out.println(dest);
        }
    }

    @Test
    public void TestRunJob() throws InterruptedException {

        //org.hibernate.Transaction tx = sessionFactory.getCurrentSession().beginTransaction();

        //createAlfrescoDestination();
        createMockedAlfrescoDestination();
        Resource dest = destinationService.getAllDestinations().stream().findFirst().get();

        JobModel jobModel = JobModel.CreateDefault("test job");
        jobModel.setDest(dest.getId());
        jobModel.setInputFolder(Arrays.asList("src/test/integration-test-resources/exampleInput/"));

        Job job = jobService.createJob(jobModel);


        //tx.commit();

        jobService.startJob(job.getId());

        Thread.sleep(1000); // wait for job start :s
        while (jobService.getJobState(job.getId()) == ECycleState.RUNNING)
            Thread.sleep(500);

    }

    private void createAlfrescoDestination() {
        AlfrescoDestinationModel destModel = new AlfrescoDestinationModel();
        destModel.setAlfPswd("admin");
        destModel.setAlfUser("admin");
        destModel.setDestinationURL("http://alex.xenit.eu:33556/alfresco/soapapi");
        destModel.setName("Jenkins apix 42");
        destModel.setNbrThreads(1);
        //destModel.setContentStoreId(-1);
        Map<?, ?> map = new HashMap<>();
        BindingResult errors = new MapBindingResult(map, "lala");
        ctrl.createDestination(destModel, errors);
    }

    private void createMockedAlfrescoDestination() {
        Resource resource = new Resource();

        ConfiguredAction action = new ConfiguredAction();
        ConfiguredSharedResource alfrescoResource = new ConfiguredSharedResource();

        alfrescoResource.setClassId(MockedAlfrescoDestination.class.getCanonicalName());//  sharedResourceClassInfoService.getClassId(AlfrescoHttpSharedResource.class));
        alfrescoResource.setName("Mock-Alfresco");
//        alfrescoResource.setParameter(AbstractAlfrescoSharedResource.PARAM_URL, destination.getDestinationURL());
//        alfrescoResource.setParameter(AbstractAlfrescoSharedResource.PARAM_USER, destination.getAlfUser());
//        alfrescoResource.setParameter(AbstractAlfrescoSharedResource.PARAM_PASSWORD, destination.getAlfPswd());


        sharedResourceService.saveConfiguredSharedResource(alfrescoResource);

        ctrl.setupAlfrescoHttpDestinationResource(resource, action, alfrescoResource, 1, "Mock-Alfresco");
        destinationService.saveDestination(resource);
    }
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
