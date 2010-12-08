package eu.xenit.move2alf;

import static org.junit.Assert.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;

public class Move2AlfTest {

	SessionFactory sessionFactory;
	
	@Before
	public void setUp() {
		sessionFactory = new Configuration().configure().buildSessionFactory();
	}
	
	@Test
	public void testHibernateConfiguration() {
		assertNotNull(sessionFactory);
	}
	
	@Test
	public void testHibernateSession() {
		Session session = sessionFactory.openSession();
		assertNotNull(session);
	}
}
