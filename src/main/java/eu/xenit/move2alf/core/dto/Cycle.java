package eu.xenit.move2alf.core.dto;

import java.sql.Timestamp;
import java.util.Set;

import eu.xenit.move2alf.common.IdObject;

public class Cycle extends IdObject {
	private Timestamp startDateTime;
	
	private Timestamp endDateTime;
	
	private Schedule schedule;
	
	private Set<RunningAction> runningActions;
	
	private Set<ProcessedDocument> processedDocuments;
	
	public Cycle() {
		
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

	public void setProcessedDocuments(Set<ProcessedDocument> processedDocuments) {
		this.processedDocuments = processedDocuments;
	}

	public Set<ProcessedDocument> getProcessedDocuments() {
		return processedDocuments;
	}
}
