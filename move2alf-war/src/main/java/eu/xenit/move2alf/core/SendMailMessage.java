package eu.xenit.move2alf.core;

public class SendMailMessage{
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
