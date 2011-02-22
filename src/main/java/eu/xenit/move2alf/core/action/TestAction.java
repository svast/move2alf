package eu.xenit.move2alf.core.action;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredActionParameter;

public class TestAction extends Action {
	private static final Logger logger = LoggerFactory
			.getLogger(TestAction.class);

	private static final TestAction instance = new TestAction();

	private TestAction() {

	}

	public static TestAction getInstance() {
		return instance;
	}

	@Override
	public void execute(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		logger.debug("Test action executed");
		for (ConfiguredActionParameter param : configuredAction
				.getConfiguredActionParameterSet()) {
			if (param.getName().equals("test")) {
				logger.debug("Output: " + param.getValue());
			}
		}
	}

}
