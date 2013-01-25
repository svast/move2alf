package eu.xenit.move2alf.repository;

import eu.xenit.move2alf.repository.alfresco.ws.Document;

public class UploadResult {
	private int status;
	private String message;
	private String reference;
	private Document document;

	public static final int VALUE_FAILED = -1;
	public static final int VALUE_OK = 1;

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

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

	public Document getDocument() {
		return document;
	}

	public void setDocument(final Document document) {
		this.document = document;
	}

	public String toString() {
		return "Status: " + status;
	}
}
