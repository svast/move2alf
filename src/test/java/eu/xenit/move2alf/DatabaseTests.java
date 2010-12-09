package eu.xenit.move2alf;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;

public class DatabaseTests {
	
	private static final String TEST_DATBASE = "move2alf_test";

	protected SessionFactory sessionFactory;
	protected Session session;

	@Before
	public void setUp() {
		sessionFactory = new Configuration()
				.configure()
				.setProperty("hibernate.connection.url",
						"jdbc:mysql://localhost/" + TEST_DATBASE + "?zeroDateTimeBehavior=convertToNull")
				.buildSessionFactory();
		session = sessionFactory.openSession();
		session.beginTransaction();
	}
	
	@After
	public void tearDown() {
		session.getTransaction().rollback();
		session.close();
	}
}
