package eu.xenit.move2alf.core.dto;

import java.sql.Timestamp;
import java.util.Set;

import eu.xenit.move2alf.common.IdObject;
import eu.xenit.move2alf.core.Report;

public class Job extends IdObject {
	private String name;

	private String description;

	private UserPswd creator;

	private Timestamp creationDateTime;

	private Timestamp lastModifyDateTime;
	
	private ConfiguredAction firstConfiguredAction;
	
	private Set<Schedule> schedules;
	
	private Set<Report> reports;

	public Job() {

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
