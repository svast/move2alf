package eu.xenit.move2alf.logic.usageservice.dto;

import java.util.Date;

import eu.xenit.move2alf.common.IdObject;


public class DocumentCounter extends IdObject {
	
	private int id;
	private int counter;
	private Date lastModifyDateTime;


	public DocumentCounter() {

	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public Date getLastModifyDateTime() {
		return lastModifyDateTime;
	}

	public void setLastModifyDateTime(Date lastModifyDateTime) {
		this.lastModifyDateTime = lastModifyDateTime;
	}
}
