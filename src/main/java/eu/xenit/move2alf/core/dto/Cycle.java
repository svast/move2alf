package eu.xenit.move2alf.core.dto;

import java.util.Date;
import java.util.Set;

import eu.xenit.move2alf.common.IdObject;
import eu.xenit.move2alf.core.enums.ECycleState;

public class Cycle extends IdObject {
	private int id;
	
	private Date startDateTime;
	
	private Date endDateTime;
	
	private Job job;
	
	private Set<RunningAction> runningActions;
	
	private Set<ProcessedDocument> processedDocuments;
	
	public Cycle() {
		
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return id;
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

	public void setJob(Job job) {
		this.job = job;
	}

	public Job getJob() {
		return job;
	}
	
	public ECycleState getState() {
		if(getEndDateTime()==null){
			return ECycleState.RUNNING;
		}
		return ECycleState.NOT_RUNNING;
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
