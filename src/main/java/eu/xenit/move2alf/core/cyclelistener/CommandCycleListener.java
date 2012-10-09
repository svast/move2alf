package eu.xenit.move2alf.core.cyclelistener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.action.ExecuteCommandAction;
import eu.xenit.move2alf.core.action.MoveDocumentsAction;
import eu.xenit.move2alf.core.action.SourceAction;

public class CommandCycleListener extends CycleListener {

	private static final Logger logger = LoggerFactory
			.getLogger(CommandCycleListener.class);

	@Override
	public void cycleEnd(int cycleId) {

	}

	@Override
	public void cycleStart(int cycleId, Map<String, Object> parameterMap) {

		Map<String, String> commandParameters = getCommandActionParameter(cycleId);
		String command = null;
		if(commandParameters != null){
			command = commandParameters.get(ExecuteCommandAction.COMMAND);
		}

		logger.debug("Command: " + command);
		if (command != null && !"".equals(command)) {
			logger.debug("Executing command " + command);

			ProcessBuilder pb = new ProcessBuilder(command);
			pb.redirectErrorStream(true);

			Map environmentMap = pb.environment();

			environmentMap.put("MOVETOALF_INPUT_PATH", getJobService()
					.getActionParameters(cycleId, SourceAction.class).get(
							SourceAction.PARAM_PATH));

			Process process = null;
			try {
				process = pb.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}

			try {
				InputStream is = process.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;

				while ((line = br.readLine()) != null) {
					logger.debug(line);

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				process.waitFor();
			} catch (InterruptedException ie) {
				logger.error("Problem running command");
			}

			logger.info("Command finished");
		}
	}

	@Override
	public void cycleStart(int cycleId) {
		// TODO Auto-generated method stub

	}

	private Map<String, String> getCommandActionParameter(int cycleId) {
		return getJobService().getActionParameters(cycleId,
				ExecuteCommandAction.class);
	}
}
