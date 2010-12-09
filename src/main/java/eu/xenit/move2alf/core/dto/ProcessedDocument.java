package eu.xenit.move2alf.core.dto;

import java.sql.Timestamp;
import java.util.Set;

import eu.xenit.move2alf.common.IdObject;
import eu.xenit.move2alf.core.enums.EProcessedDocumentStatus;

public class ProcessedDocument extends IdObject {
	private String name;
	
	private Timestamp processedDateTime;
	
	private EProcessedDocumentStatus status;
	
	private Set<ProcessedDocumentParameter> processedDocumentParameterSet;
	
	private Cycle cycle;
	
	public ProcessedDocument() {
		
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setProcessedDateTime(Timestamp processedDateTime) {
		this.processedDateTime = processedDateTime;
	}

	public Timestamp getProcessedDateTime() {
		return processedDateTime;
	}

	public void setStatus(EProcessedDocumentStatus status) {
		this.status = status;
	}

	public EProcessedDocumentStatus getStatus() {
		return status;
	}


	public Set<ProcessedDocumentParameter> getProcessedDocumentParameterSet() {
		return processedDocumentParameterSet;
	}

	public void setProcessedDocumentParameterSet(
			Set<ProcessedDocumentParameter> processedDocumentParameterSet) {
		this.processedDocumentParameterSet = processedDocumentParameterSet;
	}

	public void setCycle(Cycle cycle) {
		this.cycle = cycle;
	}

	public Cycle getCycle() {
		return cycle;
	}
}
