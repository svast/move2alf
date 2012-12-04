package eu.xenit.move2alf.core.cyclelistener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoggingCycleListener extends CycleListener {
	private static final Logger logger = LoggerFactory
			.getLogger(LoggingCycleListener.class);

	@Override
	public void cycleEnd(int cycleId) {
		logger.info("Cycle " + cycleId + " completed.");
	}

	@Override
	public void cycleStart(int cycleId) {
		logger.info("Cycle " + cycleId + " started.");
	}

}
