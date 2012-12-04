package eu.xenit.move2alf.logic;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.core.enums.EProcessedDocumentStatus;
import eu.xenit.move2alf.logic.usageservice.dto.DocumentCounter;


//Note: It seems to be impossible to add transaction management for both txManager and h2txManager
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "file:src/main/webapp/WEB-INF/applicationContext.xml", "classpath:test-applicationContext-embeddedDbs.xml" })
@Transactional("h2txManager")
@TransactionConfiguration(transactionManager = "h2txManager", defaultRollback = true)
public class JobServiceImplTest {
	
	private static final int DOCUMENT_COUNTER = 555;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private SessionFactory h2sessionFactory;
	
	@Autowired
	private JobService jobService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		initMySqlTables();
		//DbInitializer.onApplicationEvent initializes h2 db, but for the unit test we want an empty db
		deleteH2Tables();
		initH2Tables();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private void initMySqlTables () {
		Session session = sessionFactory.openSession();
		//the current transaction of txManager is not rollbacked
		List<?> list = session.createQuery("from Cycle").list();
		if ( list.size() == 0 ) {
	        Cycle cycle = new Cycle();
	        cycle.setId(1);
	        session.save(cycle);
		}
	}
	
	private void initH2Tables () {
		Session h2session = h2sessionFactory.getCurrentSession();
		
        DocumentCounter documentCounter = new DocumentCounter();
        documentCounter.setCounter(DOCUMENT_COUNTER);
        documentCounter.setLastModifyDateTime(new Date());
        h2session.save(documentCounter);
	}
	
	private void deleteH2Tables () {
		deleteH2Table("DocumentCounter");
		deleteH2Table("LicenseHistory");
	}

	private void deleteH2Table (String className) {
		Session session = h2sessionFactory.getCurrentSession();
		session.createQuery("delete from " + className).executeUpdate();
		List<?> resultList = session.createQuery("from " + className).list();
		assertEquals(0, resultList.size());
	}

	@Test
	public final void testCreateProcessedDocument_successful() {
		try {
			jobService.createProcessedDocument(1, "test", new Date(), EProcessedDocumentStatus.OK.toString(), new HashSet<ProcessedDocumentParameter>());
			assertEquals(DOCUMENT_COUNTER - 1, getDocumentCounter());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public final void testCreateProcessedDocument_mySqlException() {
		try {
			jobService.createProcessedDocument(2, "test", new Date(), EProcessedDocumentStatus.OK.toString(), new HashSet<ProcessedDocumentParameter>());
			assertEquals(DOCUMENT_COUNTER, getDocumentCounter());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public final void testCreateProcessedDocument_UploadStateIsFailed() {
		try {
			jobService.createProcessedDocument(1, "test", new Date(), EProcessedDocumentStatus.FAILED.toString(), new HashSet<ProcessedDocumentParameter>());
			assertEquals(DOCUMENT_COUNTER, getDocumentCounter());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private int getDocumentCounter () {
		Session h2session = h2sessionFactory.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<DocumentCounter> list = h2session.createQuery("from DocumentCounter").list();
		assertEquals(1, list.size());
		return list.get(0).getCounter();
	}

}
