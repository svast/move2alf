package eu.xenit.move2alf.core.dto;

import java.util.Date;
import java.util.Set;

import eu.xenit.move2alf.common.IdObject;

public class Cycle extends IdObject {
	private Date startDateTime;
	
	private Date endDateTime;
	
	private Schedule schedule;
	
	private Set<RunningAction> runningActions;
	
	public Cycle() {
		
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

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setRunningActions(Set<RunningAction> runningActions) {
		this.runningActions = runningActions;
	}

	public Set<RunningAction> getRunningActions() {
		return runningActions;
	}
}
