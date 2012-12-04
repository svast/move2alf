package eu.xenit.move2alf.logic.usageservice.dto;

import java.util.Date;

import eu.xenit.move2alf.common.IdObject;

public class LicenseHistory extends IdObject {
	
	private int id;
	private Date creationDateTime;
	private int replenishment;
	private int numberOfDocuments;


	public LicenseHistory() {

	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setCreationDateTime(Date creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	public Date getCreationDateTime() {
		return creationDateTime;
	}

	public int getReplenishment() {
		return replenishment;
	}

	public void setReplenishment(int replenishment) {
		this.replenishment = replenishment;
	}

	public int getNumberOfDocuments() {
		return numberOfDocuments;
	}

	public void setNumberOfDocuments(int numberOfDocuments) {
		this.numberOfDocuments = numberOfDocuments;
	}
	
}
