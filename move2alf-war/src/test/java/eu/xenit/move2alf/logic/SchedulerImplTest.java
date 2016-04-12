package eu.xenit.move2alf.logic;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by Michiel Huygen on 07/04/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"file:src/main/webapp/WEB-INF/applicationContext.xml","file:src/main/webapp/WEB-INF/applicationContext-security.xml" })
//@TestExecutionListeners(listeners={//ServletTestExecutionListener.class,
//        //DependencyInjectionTestExecutionListener.class,
//        //DirtiesContextTestExecutionListener.class,
//        //TransactionalTestExecutionListener.class,
//        WithSecurityContextTestExcecutionListener.class})
@WebAppConfiguration
//, "classpath:test-applicationContext-embeddedDbs.xml" })
public class SchedulerImplTest {

    @Autowired
    Scheduler scheduler;

    /**
     * The scheduler should be able to run as a system user internally, so it should not need user authentication
     */
    @Test
    public void TestSchedulesCanReloadWithoutUser() {
        scheduler.reloadSchedules();
    }
}
