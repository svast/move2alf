package eu.xenit.move2alf.core.action;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public class DebugAction extends Action {
	private static final Logger logger = LoggerFactory
			.getLogger(DebugAction.class);

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("------------------------------");
		for (String key : parameterMap.keySet()) {
			logger.debug(key + "\t-\t" + parameterMap.get(key).toString());
		}
		logger.debug("------------------------------");
	}

}
