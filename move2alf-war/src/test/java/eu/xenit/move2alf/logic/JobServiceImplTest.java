package eu.xenit.move2alf.logic;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashSet;
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

import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.core.enums.EProcessedDocumentStatus;


//Note: It seems to be impossible to add transaction management for both txManager and h2txManager
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "file:src/main/webapp/WEB-INF/applicationContext.xml", "classpath:test-applicationContext-embeddedDbs.xml" })
public class JobServiceImplTest {

	@Autowired
	private SessionFactory sessionFactory;

	
	@Autowired
	private JobService jobService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
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


}
