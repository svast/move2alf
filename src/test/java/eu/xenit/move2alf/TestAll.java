package eu.xenit.move2alf;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	HibernateTests.class,
	UserPswdTests.class,
	ParameterDefinitionTests.class
})
public class TestAll {
}
