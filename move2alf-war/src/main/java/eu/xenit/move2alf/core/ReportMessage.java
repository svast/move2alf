/**
 * 
 */
package eu.xenit.move2alf.core;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.pipeline.AbstractMessage;

public class ReportMessage extends AbstractMessage{
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
}