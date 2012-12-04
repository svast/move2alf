/*
 * Created on May 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package eu.xenit.move2alf.common;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dirk
 * 
 * 
 *         this class is used to continously empty the buffers of Runtime.exec
 * 
 *         based on code snippet from
 *         http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps_p.html
 */

public class StreamGobbler extends Thread {
	private static Logger logger = LoggerFactory.getLogger(StreamGobbler.class);

	InputStream is;

	String type;

	String result = null;

	boolean ready = false;

	public StreamGobbler(InputStream is, String type) {
		this.is = is;
		this.type = type;
	}

	public void run() {
		logger.debug("*************** " + type + " *********************");
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		try {
			StringBuffer buffer = new StringBuffer();
			while ((line = br.readLine()) != null) {
				buffer.append(line).append("\n");
				logger.debug(line);
			}
			result = buffer.toString();
			ready = true;

			br.close();
			isr.close();
		} catch (IOException ioe) {
			logger.error("IOException in StreamGobbler", ioe);
			ready = true;
		}
		logger.debug("********************************************");
	}

	public String getResult() {
		return result;
	}

	public boolean isReady() {
		return ready;
	}

	public static String executeCommand(Process p) throws ProcessErrorException {
		// read the stdInput and stdError buffers
		// any error message?
		StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(),
				"ERROR");

		// any output?
		StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(),
				"OUTPUT");

		// kick them off
		errorGobbler.start();
		outputGobbler.start();

		try {
			int returnCode = p.waitFor();
			while (!outputGobbler.isReady()) {
				Thread.sleep(10);
				logger.debug("Waiting on Output Thread");
			}
			String output = outputGobbler.getResult();
			while (!errorGobbler.isReady()) {
				Thread.sleep(10);
				logger.debug("Waiting on Error Thread");
			}
			String error = errorGobbler.getResult();

			logger.debug(error);
			logger.debug(output);

			if (error.length() > 0) {
				logger.warn(error);
				throw new ProcessErrorException(error);
			}
			if (returnCode != 0) {
				logger.warn("Errorcode for Runtime process: " + returnCode);
				throw new ProcessErrorException(
						"Errorcode for Runtime process: " + returnCode);
			}
			return output;
		} catch (InterruptedException e) {
			logger.warn("Command interrupted", e);
			throw new ProcessErrorException(e.getMessage());
		}
	}
}