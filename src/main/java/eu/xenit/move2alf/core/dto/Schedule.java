package eu.xenit.move2alf.core.dto;

import java.sql.Timestamp;
import java.util.Date;

import eu.xenit.move2alf.common.IdObject;
import eu.xenit.move2alf.core.enums.EScheduleState;

public class Schedule extends IdObject {
	private int id;
	
	private Job job;
	
	private UserPswd creator;
	
	private Date creationDateTime;
	
	private Date lastModifyDateTime;
	
	private String state;
	
	private Date startDateTime;
	
	private Date endDateTime;
	
	private String quartzScheduling;

	public Schedule() {
	
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
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

	public void setCreationDateTime(Date creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	public Date getCreationDateTime() {
		return creationDateTime;
	}

	public void setLastModifyDateTime(Date lastModifyDateTime) {
		this.lastModifyDateTime = lastModifyDateTime;
	}

	public Date getLastModifyDateTime() {
		return lastModifyDateTime;
	}

	public void setState(String state) {
		this.state="TEST";
	}

	public String getState() {
		return state;
	}

	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	public Date getStartDateTime() {
		return startDateTime;
	}

	public void setEndDateTime(Date endDateTime) {
		this.endDateTime = endDateTime;
	}

	public Date getEndDateTime() {
		return endDateTime;
	}

	public void setQuartzScheduling(String quartzScheduling) {
		this.quartzScheduling = quartzScheduling;
	}

	public String getQuartzScheduling() {
		return quartzScheduling;
	}
}
