package eu.xenit.move2alf;

import eu.xenit.move2alf.integrationtests.JobsTests;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
	HibernateTests.class,
	UserPswdTests.class,
	JobsTests.class
})
public class TestAll {
}
