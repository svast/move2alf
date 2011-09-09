package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.simpleaction.SAFilter;

public class FilterAction extends Action {

	private static final Logger logger = LoggerFactory
			.getLogger(FilterAction.class);

	@Override
	public void execute(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {

		String extension = configuredAction
				.getParameter(SAFilter.PARAM_EXTENSION);

		File file = (File) parameterMap.get(Parameters.PARAM_FILE);

		if ("*".equals(extension) || "".equals(extension) || extension == null) {
			logger.debug("No extension filter");
			ConfiguredAction nextAction = configuredAction
					.getAppliedConfiguredActionOnSuccess();
			if (nextAction != null) {
				getJobService().executeAction(
						(Integer) parameterMap.get("cycle"), nextAction,
						parameterMap);
			}
		} else if (file.getPath().toLowerCase().endsWith(
				extension.toLowerCase())) {
			logger.debug("File found with correct extension - continue");
			ConfiguredAction nextAction = configuredAction
					.getAppliedConfiguredActionOnSuccess();
			if (nextAction != null) {
				getJobService().executeAction(
						(Integer) parameterMap.get("cycle"), nextAction,
						parameterMap);
			}
		} else {
			logger.debug("File does not have the correct extension - skip.");

			CountDownLatch countDown = (CountDownLatch) parameterMap
					.get(Parameters.PARAM_COUNTER);

			countDown.countDown();

			return;
		}
	}

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {

	}

	@Override
	public String getCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
