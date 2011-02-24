package eu.xenit.move2alf.core.action;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfiguredObject;

public class DebugAction extends Action {
	private static final Logger logger = LoggerFactory
			.getLogger(DebugAction.class);

	@Override
	protected void executeImpl(ConfiguredObject configuredAction,
			Map<String, Object> parameterMap) {
		logger.debug("------------------------------");
		for (String key : parameterMap.keySet()) {
			logger.debug(key + "\t-\t" + parameterMap.get(key).toString());
		}
		logger.debug("------------------------------");
	}

}
