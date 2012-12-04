package eu.xenit.move2alf;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/*
 * Abstract base class for integration tests.
 * 
 * Tests integration with database (trough Hibernate), the Spring container and Spring Security.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( { "file:src/main/webapp/WEB-INF/applicationContext.xml",
		"file:src/main/webapp/WEB-INF/applicationContext-security.xml" })
@Transactional
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = true)
public abstract class IntegrationTests {

	protected SessionFactory sessionFactory;

	protected Session session;

	private AuthenticationManager authenticationManager;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Autowired
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}
	
	@Before
	public void setUp() {
		session = getSessionFactory().getCurrentSession();
	}

	protected void loginAsAdmin() {
		Authentication auth = getAuthenticationManager().authenticate(
				new UsernamePasswordAuthenticationToken("admin", "admin"));
		SecurityContextHolder.getContext().setAuthentication(auth);
	}
}
