package eu.xenit.move2alf;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;


/*
 * Abstract base class for integration tests.
 *
 * Tests integration with database (trough Hibernate), the Spring container and Spring Security.
 * Created by mhgam on 24/03/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "file:src/test/resources/test-applicationContext.xml", // The order is important here!!!!
        "file:src/main/webapp/WEB-INF/applicationContext.xml",
        "file:src/main/webapp/WEB-INF/applicationContext-security.xml"})
//@Transactional
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = true)
public abstract class BaseIntegrationTest {
    @Autowired
    private ApplicationContext ctx;

    protected SessionFactory sessionFactory;
    private AuthenticationManager authenticationManager;

    public static void loginAsAdmin(AuthenticationManager authenticationManager) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("admin", "admin"));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

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


    protected void loginAsAdmin() {
        loginAsAdmin(authenticationManager);
    }

    @After
    public void closeH2()
    {

        sessionFactory.close();
    }



//    @Before
//    public void resetH2Database() {
//        org.hibernate.classic.Session sess = null;
//        try {
//
////            IDatabaseConnection dbConn = new DatabaseDataSourceConnection(
////                    ctx.getBean(DataSource.class)
////            );
////            dbConn.getConnection().prepareStatement("SET DATABASE REFERENTIAL INTEGRITY FALSE").execute();
//
//
//            sess = sessionFactory.openSession();
//
//            org.hibernate.Transaction tx = sess.beginTransaction();
//
//            hqlTruncate("ConfiguredAction", sess);
//            hqlTruncate("ConfiguredSharedResource", sess);
//            hqlTruncate("Cycle", sess);
//            hqlTruncate("Job", sess);
//            hqlTruncate("ProcessedDocument", sess);
//            hqlTruncate("Schedule", sess);
//            hqlTruncate("UserPswd", sess);
//            hqlTruncate("Resource", sess);
//            tx.commit();
//        } finally {
//            if (sess != null)
//                sess.close();
//        }
//
//    }
//
//    public int hqlTruncate(String myTable, Session sess) {
//        String hql = String.format("delete from %s", myTable);
//        Query query = sess.createQuery(hql);
//        return query.executeUpdate();
//    }
}
