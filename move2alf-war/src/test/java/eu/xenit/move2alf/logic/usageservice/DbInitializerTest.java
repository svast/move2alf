package eu.xenit.move2alf.logic.usageservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
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

import eu.xenit.move2alf.logic.usageservice.DbInitializer.ActionEnum;
import eu.xenit.move2alf.logic.usageservice.dto.DocumentCounter;
import eu.xenit.move2alf.logic.usageservice.dto.LicenseHistory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "file:src/main/webapp/WEB-INF/applicationContext.xml", "classpath:test-applicationContext-embeddedDbs.xml" })
@Transactional
@TransactionConfiguration(transactionManager = "h2txManager", defaultRollback = true)
public class DbInitializerTest {

	private static final Date DATE_02_OCT_2012_14h30 = new LocalDateTime(2012, 10, 02, 14, 30).toDate();
	private static final LocalDate DATE_02_OCT_2012 = new LocalDate(2012, 10, 02);
	private static final LocalDate DATE_07_NOV_2012 = new LocalDate(2012, 11, 07);
	private static final LocalDate DATE_16_NOV_2012 = new LocalDate(2012, 11, 16);
	private static final LocalDate DATE_02_OCT_2013 = new LocalDate(2013, 10, 02);
	private static final int INT_1000 = 1000;
	private static final int INT_5000 = 5000;
	
	@Autowired
	private SessionFactory h2sessionFactory;

	@Autowired
	private DbInitializer dbInitializer;
	
	@Autowired
	private LicenseFilenameService licenseFilenameService;

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		//DbInitializer.onApplicationEvent initializes h2 db, but for the unit test we want an empty db
		deleteTables();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	//-----------------------------------------------------------------------------
	
	@Test
	public final void testGetMaxInstallationDate() throws Exception {
		loadLicense("move2alf-06nov-01oct-15oct-5000-1000.lic");
		assertEquals(DATE_16_NOV_2012, dbInitializer.getMaxInstallationDate());
	}
	
	//-----------------------------------------------------------------------------
	
	@Test
	public final void testGetNextReplenishmentDate() throws Exception {
		initTables();
		assertEquals(DATE_02_OCT_2013, dbInitializer.getNextReplenishmentDate());
	}
	
	//-----------------------------------------------------------------------------
	
	@Test
	public final void testGetAction_invalidLicenseName () throws Exception {
		loadLicense("xxx.lic");
		assertNull(dbInitializer.getLicense());
		assertEquals(ActionEnum.DO_NOTHING, dbInitializer.getAction());
	}
	
	@Test
	public final void testGetAction_oldLicense () throws Exception {
		loadLicense("move2alf-06nov-01oct-15oct-noAmounts.lic");
		assertNotNull(dbInitializer.getLicense());
		
		dbInitializer.setCurDate(DATE_07_NOV_2012);
		assertEquals(ActionEnum.DO_NOTHING, dbInitializer.getAction());
	}
	
	@Test
	public final void testGetAction_firstStartup_beforeMaxInstallationDate() throws Exception {
		loadLicense("move2alf-06nov-01oct-15oct-5000-1000.lic");
		assertNotNull(dbInitializer.getLicense());
		
		dbInitializer.setCurDate(DATE_07_NOV_2012);
		assertEquals(ActionEnum.CREATE_TABLES, dbInitializer.getAction());
	}
	
	@Test
	public final void testGetAction_firstStartup_afterMaxInstallationDate () throws Exception {
		loadLicense("move2alf-06nov-01oct-15oct-5000-1000.lic");
		assertNotNull(dbInitializer.getLicense());
		
		dbInitializer.setCurDate(DATE_02_OCT_2013);
		assertEquals(ActionEnum.DO_NOTHING, dbInitializer.getAction());
	}
	
	@Test
	public final void testGetAction_nextStartup_withoutReplenishment () throws Exception {
		loadLicense("move2alf-06nov-01oct-15oct-5000-1000.lic");
		assertNotNull(dbInitializer.getLicense());
		
		initTables();
		
		dbInitializer.setCurDate(DATE_02_OCT_2012);
		assertEquals(ActionEnum.DO_NOTHING, dbInitializer.getAction());
	}
	
	@Test
	public final void testGetAction_nextStartup_withReplenishment () throws Exception {
		loadLicense("move2alf-06nov-01oct-15oct-5000-1000.lic");
		assertNotNull(dbInitializer.getLicense());
		
		initTables();
		
		dbInitializer.setCurDate(DATE_02_OCT_2013);
		assertEquals(ActionEnum.ADD_REPLENISHMENT, dbInitializer.getAction());
	}
	
	//-----------------------------------------------------------------------------
	
	@Test
	public final void testH2dbExists () throws Exception {
		assertEquals(false, dbInitializer.h2dbExists());
		initTables();
		assertEquals(true, dbInitializer.h2dbExists());
	}
	
	//-----------------------------------------------------------------------------
	
	@Test
	public final void testCreateTables () throws Exception {
		loadLicense("move2alf-06nov-01oct-15oct-5000-1000.lic");
		dbInitializer.createTables();
		
		@SuppressWarnings("unchecked")
		List<DocumentCounter> docCounterList = h2sessionFactory.getCurrentSession().createQuery("from DocumentCounter").list();
		assertEquals(1, docCounterList.size());
		assertEquals(INT_5000, docCounterList.get(0).getCounter());
		
		@SuppressWarnings("unchecked")
		List<LicenseHistory> licenseHistoryList = h2sessionFactory.getCurrentSession().createQuery("from LicenseHistory").list();
		assertEquals(1, licenseHistoryList.size());
		assertEquals(INT_5000, licenseHistoryList.get(0).getReplenishment());
		assertEquals(INT_5000, licenseHistoryList.get(0).getNumberOfDocuments());
	}
	
	//-----------------------------------------------------------------------------
	
	@Test
	public final void testAddYearlyReplenishment () throws Exception {
		loadLicense("move2alf-06nov-01oct-15oct-5000-1000.lic");
		dbInitializer.createTables();
		dbInitializer.addYearlyReplenishment();
		
		@SuppressWarnings("unchecked")
		List<DocumentCounter> docCounterList = h2sessionFactory.getCurrentSession().createQuery("from DocumentCounter").list();
		assertEquals(1, docCounterList.size());
		assertEquals(INT_5000 + INT_1000, docCounterList.get(0).getCounter());
		
		@SuppressWarnings("unchecked")
		List<LicenseHistory> licenseHistoryList = h2sessionFactory.getCurrentSession().createQuery("from LicenseHistory order by creationDateTime asc").list();
		assertEquals(2, licenseHistoryList.size());
		LicenseHistory licenseHistory1 = licenseHistoryList.get(0);
		LicenseHistory licenseHistory2 = licenseHistoryList.get(1);
		
		assertEquals(INT_5000, licenseHistory1.getReplenishment());
		assertEquals(INT_5000, licenseHistory1.getNumberOfDocuments());
		
		assertEquals(INT_1000, licenseHistory2.getReplenishment());
		assertEquals(INT_5000 + INT_1000, licenseHistory2.getNumberOfDocuments());
	}
	
	//-----------------------------------------------------------------------------
	
	private void loadLicense (String licenseFilename) {
		licenseFilenameService.setLicenseFilename(licenseFilename);
		dbInitializer.loadLicense();
	}
	
	private void deleteTables () {
		deleteTable("DocumentCounter");
		deleteTable("LicenseHistory");
	}

	private void deleteTable (String className) {
		Session session = h2sessionFactory.getCurrentSession();
		session.createQuery("delete from " + className).executeUpdate();
		List<?> resultList = session.createQuery("from " + className).list();
		assertEquals(0, resultList.size());
	}
	
	private void initTables () {
		dbInitializer.createTables(INT_1000, DATE_02_OCT_2012_14h30);
	}
}
