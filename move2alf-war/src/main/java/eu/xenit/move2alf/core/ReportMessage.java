/**
 * 
 */
package eu.xenit.move2alf.core;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;

public class ReportMessage {
	int cycleId;
	String name;
	Date date;
	String state;
	Set<ProcessedDocumentParameter> params = new HashSet<ProcessedDocumentParameter>();
	
	public ReportMessage(int cycleId, String name, Date date, String state, Set<ProcessedDocumentParameter> params) {
		this.cycleId = cycleId;
		this.name = name;
		this.date = date;
		this.state = state;
		this.params = params;
	}
}