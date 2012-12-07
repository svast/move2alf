package eu.xenit.move2alf.repository;

import eu.xenit.move2alf.repository.alfresco.ws.Document;

public class IllegalDuplicateException extends IllegalDocumentException {

	public IllegalDuplicateException(Document document, String message) {
		super(document, message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8661202074567777000L;

}
