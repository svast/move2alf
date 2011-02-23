package eu.xenit.move2alf.web.dto;

import java.util.Date;

public class JobInfo {

	private int jobId;
	private String jobName;
	private int cycleId = -1;
	private Date cycleStartDateTime;
	private String scheduleState;
	
	public JobInfo(){		
	}
	
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public int getJobId() {
		return jobId;
	}
	
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobName() {
		return jobName;
	}
	
	public void setCycleId(int cycleId) {
		this.cycleId = cycleId;
	}

	public int getCycleId() {
		return cycleId;
	}
	
	public void setCycleStartDateTime(Date cycleStartDateTime) {
		this.cycleStartDateTime = cycleStartDateTime;
	}

	public Date getCycleStartDateTime() {
		return cycleStartDateTime;
	}
	
	public void setScheduleState(String scheduleState) {
		this.scheduleState = scheduleState;
	}

	public String getScheduleState() {
		return scheduleState;
	}
}
