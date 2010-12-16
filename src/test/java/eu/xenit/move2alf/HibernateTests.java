package eu.xenit.move2alf;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class HibernateTests extends IntegrationTests {
	
	@Test
	public void testHibernateConfiguration() {
		assertNotNull(sessionFactory);
	}

	@Test
	public void testHibernateSession() {
		assertNotNull(session);
	}
}
