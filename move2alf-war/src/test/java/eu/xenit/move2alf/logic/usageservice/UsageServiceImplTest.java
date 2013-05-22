package eu.xenit.move2alf.logic.usageservice;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import eu.xenit.move2alf.logic.usageservice.dto.DocumentCounter;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "file:src/main/webapp/WEB-INF/applicationContext.xml", "classpath:test-applicationContext-embeddedDbs.xml" })
@Transactional
@TransactionConfiguration(transactionManager = "h2txManager", defaultRollback = true)
public class UsageServiceImplTest {
	
	private static final int DOCUMENT_COUNTER_555 = 555;
	private static final int DOCUMENT_COUNTER_0 = 0;
	private static final int DOCUMENT_COUNTER_NEG = -1;

	@Autowired
	private SessionFactory h2sessionFactory;
	
	@Autowired
	private LicenseFilenameService licenseFilenameService;

	@Autowired
	private UsageService usageService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		//DbInitializer.onApplicationEvent initializes h2 db, but for the unit test we want an empty db
		deleteDocumentCounterTable();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	//-----------------------------------------------------------------------------
	
	@Test
	public final void testGetValidationFailureCause_noLicense() {
		licenseFilenameService.setLicenseFilename("xxx.lic");
		assertEquals("nolicense", usageService.getValidationFailureCause());
	}
	
	@Test
	public final void testGetValidationFailureCause_licenseExpired() {
		licenseFilenameService.setLicenseFilename("move2alf-06nov-01oct-15oct-5000-1000.lic");
		assertEquals("validation", usageService.getValidationFailureCause());
	}
	
	@Test
	public final void testGetValidationFailureCause_docCounterBlocked() {
		licenseFilenameService.setLicenseFilename("move2alf-15nov-01oct-noExpDate-5000-1000.lic");
		createDocumentCounterTable(DOCUMENT_COUNTER_0);
		assertEquals("blocked", usageService.getValidationFailureCause());
	}
	
	@Test
	public final void testGetValidationFailureCause_ok() {
		licenseFilenameService.setLicenseFilename("move2alf-15nov-01oct-noExpDate-5000-1000.lic");
		createDocumentCounterTable(DOCUMENT_COUNTER_555);
		assertEquals(null, usageService.getValidationFailureCause());
	}
	
	//-----------------------------------------------------------------------------

	@Test
	public final void testIsBlockedByDocumentCounter_notBlocked() {
		createDocumentCounterTable(DOCUMENT_COUNTER_555);
		assertEquals(false, usageService.isBlockedByDocumentCounter());
	}

	@Test
	public final void testIsBlockedByDocumentCounter_blocked_counterIsZero() {
		createDocumentCounterTable(DOCUMENT_COUNTER_0);
		assertEquals(true, usageService.isBlockedByDocumentCounter());
	}

	@Test
	public final void testIsBlockedByDocumentCounter_blocked_counterIsNegative() {
		createDocumentCounterTable(DOCUMENT_COUNTER_NEG);
		assertEquals(true, usageService.isBlockedByDocumentCounter());
	}

	@Test
	public final void testIsBlockedByDocumentCounter_blocked_emptyTable() {
		List<?> docCounterList = h2sessionFactory.getCurrentSession().createQuery("from DocumentCounter").list();
		assertEquals(0, docCounterList.size());
		assertEquals(true, usageService.isBlockedByDocumentCounter());
	}
	
	//-----------------------------------------------------------------------------

	@Test
	public final void testGetDocumentCounter() {
		createDocumentCounterTable(DOCUMENT_COUNTER_555);
		assertEquals(DOCUMENT_COUNTER_555, usageService.getDocumentCounter());
	}

	@Test
	public final void testGetDocumentCounter_emptyTable() {
		List<?> docCounterList = h2sessionFactory.getCurrentSession().createQuery("from DocumentCounter").list();
		assertEquals(0, docCounterList.size());
		assertEquals(0, usageService.getDocumentCounter());
	}
	
	//-----------------------------------------------------------------------------

	@Test
	public final void testDecrementDocumentCounter() {
		createDocumentCounterTable(DOCUMENT_COUNTER_555);
		assertEquals(DOCUMENT_COUNTER_555, getCounter());
		usageService.decrementDocumentCounter();
		assertEquals(DOCUMENT_COUNTER_555 - 1, getCounter());
	}
	
	//-----------------------------------------------------------------------------

	private void createDocumentCounterTable (int counter) {
		Session session = h2sessionFactory.getCurrentSession();
		
        DocumentCounter documentCounter = new DocumentCounter();
        documentCounter.setCounter(counter);
        documentCounter.setLastModifyDateTime(new Date());
        session.save(documentCounter);
	}
	
	private void deleteDocumentCounterTable () {	
		Session h2session = h2sessionFactory.getCurrentSession();
		h2session.createQuery("delete from DocumentCounter").executeUpdate();
		List<?> docCounterList = h2session.createQuery("from DocumentCounter").list();
		assertEquals(0, docCounterList.size());
	}
	
	private int getCounter () {		
		Session h2session = h2sessionFactory.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<DocumentCounter> docCounterList = h2session.createQuery("from DocumentCounter").list();
		DocumentCounter counter = docCounterList.get(0);
		return counter.getCounter();
	}

}
