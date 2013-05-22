package eu.xenit.move2alf;

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
