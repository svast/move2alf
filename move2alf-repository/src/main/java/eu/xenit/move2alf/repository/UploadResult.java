package eu.xenit.move2alf.repository;

import org.alfresco.webservice.repository.UpdateResult;

public class UploadResult {
	int status;
	String message;
	String reference;
	
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public static final int VALUE_FAILED = -1;
	public static final int VALUE_OK = 1;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}	
	public String toString() {
		return "Status: " + status;
	}
}
