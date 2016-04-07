/**
 * 
 */
package eu.xenit.move2alf.core;

import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;

import java.util.Date;
import java.util.Set;

public class ReportMessage{
	public final String name;
	public final Date date;
	public final String state;
	public final String reference;
	public final Set<ProcessedDocumentParameter> params;
	
	public ReportMessage(String name, Date date, String state, Set<ProcessedDocumentParameter> params, String reference) {
		this.name = name;
		this.date = date;
		this.state = state;
		this.params = params;
		this.reference = reference;
	}

	@Override
	public String toString() {
		return "ReportMessage{" +
				"name='" + name + '\'' +
				", date=" + date +
				", state='" + state + '\'' +
				", reference='" + reference + '\'' +
				", params=" + params +
				'}';
	}
}