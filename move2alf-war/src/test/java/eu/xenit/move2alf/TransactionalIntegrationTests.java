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
 * Base class for integration tests which should run in their own transaction
 */
@Transactional
public abstract class TransactionalIntegrationTests extends BaseIntegrationTest {
	protected Session session;

	@Before
	public void setUp() {
		session = getSessionFactory().getCurrentSession();
	}

}
