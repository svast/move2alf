package eu.xenit.move2alf.core.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public class ExecuteCommandAction extends Action {

	public static final String PARAM_MOVE_BEFORE_PROCESSING_PATH = "moveBeforeProcessingPath";
	public static final String PARAM_MOVE_BEFORE_PROCESSING = "moveBeforeProcessing";
	public static final String COMMAND = "command";

	private static final Logger logger = LoggerFactory
			.getLogger(ExecuteCommandAction.class);

	@Override
	public void execute(final ConfiguredAction configuredAction,
			final Map<String, Object> parameterMap) {
		// TODO Auto-generated method stub

		final String stage = configuredAction
				.getParameter(Parameters.PARAM_STAGE);

		final CountDownLatch countDown = (CountDownLatch) parameterMap
				.get(Parameters.PARAM_COUNTER);

		if ("after".equals(stage) && countDown.getCount() == 1) {

			final String command = configuredAction
					.getParameter(Parameters.PARAM_COMMAND);
			logger.debug("Command: " + command);
			if (command != null && !"".equals(command)) {
				logger.debug("Executing command " + command);

				final ProcessBuilder pb = new ProcessBuilder(command);
				pb.redirectErrorStream(true);

				final Map environmentMap = pb.environment();

				environmentMap.put("MOVETOALF_INPUT_PATH",
						configuredAction.getParameter(SourceAction.PARAM_PATH));

				final String moveBeforeProcessing = configuredAction
						.getParameter(MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING_PATH);
				final String moveAfterLoad = configuredAction
						.getParameter(MoveDocumentsAction.PARAM_MOVE_AFTER_LOAD_PATH);
				final String moveNotLoaded = configuredAction
						.getParameter(MoveDocumentsAction.PARAM_MOVE_NOT_LOADED_PATH);

				if ("true"
						.equals(configuredAction
								.getParameter(MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING))
						&& moveBeforeProcessing != null) {
					environmentMap.put("MOVETOALF_BEFORE_LOAD",
							moveBeforeProcessing);
				}

				if ("after".equals(stage)) {
					if ("true"
							.equals(configuredAction
									.getParameter(MoveDocumentsAction.PARAM_MOVE_AFTER_LOAD))
							&& moveAfterLoad != null) {
						environmentMap.put("MOVETOALF_AFTER_LOAD",
								moveAfterLoad);
					}
					if ("true"
							.equals(configuredAction
									.getParameter(MoveDocumentsAction.PARAM_MOVE_NOT_LOADED))
							&& moveNotLoaded != null) {
						environmentMap.put("MOVETOALF_NOT_LOAD", moveNotLoaded);
					}
				}

				Process process = null;
				try {
					process = pb.start();
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}

				try {
					final InputStream is = process.getInputStream();
					final InputStreamReader isr = new InputStreamReader(is);
					final BufferedReader br = new BufferedReader(isr);
					String line;

					while ((line = br.readLine()) != null) {
						logger.debug(line);

					}
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					process.waitFor();
				} catch (final InterruptedException ie) {
					logger.error("Problem running command");
				}

				logger.info("Command finished");
			}
		}

		// Go to next action
		final ConfiguredAction nextAction = configuredAction
				.getAppliedConfiguredActionOnSuccess();
		if (nextAction != null) {
			getJobService().executeAction((Integer) parameterMap.get("cycle"),
					nextAction, parameterMap);
		}
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

	@Override
	protected void executeImpl(final ConfiguredAction configuredAction,
			final Map<String, Object> parameterMap) {
		// TODO Auto-generated method stub

	}

}
