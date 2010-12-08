package eu.xenit.move2alf.core.dto;

import java.sql.Timestamp;

import eu.xenit.move2alf.common.IdObject;
import eu.xenit.move2alf.core.enums.EScheduleState;

public class Schedule extends IdObject {
	private Job job;
	
	private UserPswd creator;
	
	private Timestamp creationDateTime;
	
	private Timestamp lastModifyDateTime;
	
	private EScheduleState state;
	
	private Timestamp startDateTime;
	
	private Timestamp endDateTime;
	
	private String quartzScheduling;

	public Schedule() {
	
	}
	
	public void setJob(Job job) {
		this.job = job;
	}

	public Job getJob() {
		return job;
	}

	public void setCreator(UserPswd creator) {
		this.creator = creator;
	}

	public UserPswd getCreator() {
		return creator;
	}

	public void setCreationDateTime(Timestamp creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	public Timestamp getCreationDateTime() {
		return creationDateTime;
	}

	public void setLastModifyDateTime(Timestamp lastModifyDateTime) {
		this.lastModifyDateTime = lastModifyDateTime;
	}

	public Timestamp getLastModifyDateTime() {
		return lastModifyDateTime;
	}

	public void setState(EScheduleState state) {
		this.state = state;
	}

	public EScheduleState getState() {
		return state;
	}

	public void setStartDateTime(Timestamp startDateTime) {
		this.startDateTime = startDateTime;
	}

	public Timestamp getStartDateTime() {
		return startDateTime;
	}

	public void setEndDateTime(Timestamp endDateTime) {
		this.endDateTime = endDateTime;
	}

	public Timestamp getEndDateTime() {
		return endDateTime;
	}

	public void setQuartzScheduling(String quartzScheduling) {
		this.quartzScheduling = quartzScheduling;
	}

	public String getQuartzScheduling() {
		return quartzScheduling;
	}
}
