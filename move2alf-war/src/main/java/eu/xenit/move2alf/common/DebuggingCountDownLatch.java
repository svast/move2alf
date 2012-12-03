package eu.xenit.move2alf.common;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebuggingCountDownLatch extends CountDownLatch {
	private static final Logger logger = LoggerFactory.getLogger(DebuggingCountDownLatch.class);
	
	public DebuggingCountDownLatch(int count) {
		super(count);
		logger.debug("Creating countdownlatch with count " + count);
	}
	
	@Override
	public void countDown() {
		super.countDown();
		logger.debug("Counting down, new count = " + super.getCount());
	}
}
