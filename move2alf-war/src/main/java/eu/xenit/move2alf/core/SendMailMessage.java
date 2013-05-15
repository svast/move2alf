package eu.xenit.move2alf.core;

import eu.xenit.move2alf.pipeline.AbstractMessage;

public class SendMailMessage extends AbstractMessage{
	private final int cycleId;
	private final String jobName;
	
	public SendMailMessage(int cycleId, String jobName) {
		this.cycleId = cycleId;
		this.jobName = jobName;
	}
	
	public int getCycleId() {
		return this.cycleId;
	}
	
	public String getJobName() {
		return this.jobName;
	}
}
