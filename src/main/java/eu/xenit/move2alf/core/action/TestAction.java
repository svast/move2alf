package eu.xenit.move2alf.core.action;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public class TestAction extends Action {
	private static final Logger logger = LoggerFactory
			.getLogger(TestAction.class);

	@Override
	public void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		// TODO: check if class name of configured action matches class of this
		// action?
		logger.debug("Test action executed");
		logger.debug("Output (map): " + configuredAction.getParameter("test"));
	}

}
