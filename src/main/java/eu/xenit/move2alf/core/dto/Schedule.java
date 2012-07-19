package eu.xenit.move2alf.core.dto;

import java.util.Date;

import eu.xenit.move2alf.common.IdObject;
import eu.xenit.move2alf.core.enums.ECycleState;

public class Schedule extends IdObject {
	private int id;
	
	private Job job;
	
	private UserPswd creator;
	
	private Date creationDateTime;
	
	private Date lastModifyDateTime;
	
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

	public void setQuartzScheduling(String quartzScheduling) {
		this.quartzScheduling = quartzScheduling;
	}

	public String getQuartzScheduling() {
		return quartzScheduling;
	}
}
