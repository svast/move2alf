package eu.xenit.move2alf.core.dto;

import java.util.Date;
import java.util.Set;

import eu.xenit.move2alf.common.IdObject;
import eu.xenit.move2alf.core.Report;

public class Job extends IdObject {
	private int id;
	
	private String name;

	private String description;

	private UserPswd creator;

	private Date creationDateTime;

	private Date lastModifyDateTime;
	
	private ConfiguredAction firstConfiguredAction;
	
	private Set<Schedule> schedules;
	
	private Set<Report> reports;

	public Job() {

	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
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

	public void setFirstConfiguredAction(ConfiguredAction firstConfiguredAction) {
		this.firstConfiguredAction = firstConfiguredAction;
	}

	public ConfiguredAction getFirstConfiguredAction() {
		return firstConfiguredAction;
	}

	public void setSchedules(Set<Schedule> schedules) {
		this.schedules = schedules;
	}

	public Set<Schedule> getSchedules() {
		return schedules;
	}

	public void setReports(Set<Report> reports) {
		this.reports = reports;
	}

	public Set<Report> getReports() {
		return reports;
	}
}
