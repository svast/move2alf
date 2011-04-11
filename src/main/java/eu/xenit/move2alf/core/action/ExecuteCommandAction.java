package eu.xenit.move2alf.core.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public class ExecuteCommandAction extends Action{

	private static final Logger logger = LoggerFactory
	.getLogger(ExecuteCommandAction.class);
	
	@Override
	public void execute(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap){

		String command = configuredAction
					.getParameter(Parameters.PARAM_COMMAND);

		if(command != null && !"".equals(command))
		{
			logger.debug("Executing command "+ command);
			
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.redirectErrorStream(true);
			Process process = null;
			try {
				process = pb.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			
			try {
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
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		// TODO Auto-generated method stub
		
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
