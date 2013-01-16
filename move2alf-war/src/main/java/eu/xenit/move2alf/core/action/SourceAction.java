package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.DebuggingCountDownLatch;
import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.sourcesink.SourceSink;

public class SourceAction extends Action {
	public static final String PARAM_PATH = "path";
	public static final String PARAM_RECURSIVE = "recursive";

	private static final Logger logger = LoggerFactory
			.getLogger(SourceAction.class);

	@Override
	public void execute(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		
	}

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		// empty; SourceAction overrides the execute method to execute the next
		// action for each file separately
	}

	@Override
	public String getCategory() {
		return ConfigurableObject.CAT_DEFAULT;
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getName() {
		return "Source";
	}

}
