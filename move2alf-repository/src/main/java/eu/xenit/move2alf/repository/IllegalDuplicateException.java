package eu.xenit.move2alf.repository;

import org.alfresco.webservice.types.Reference;

import eu.xenit.move2alf.repository.alfresco.ws.Document;

public class IllegalDuplicateException extends IllegalDocumentException {
	Reference ref;
	
	public Reference getRef() {
		return ref;
	}

	public void setRef(Reference ref) {
		this.ref = ref;
	}

	public IllegalDuplicateException(Document document, String message, Reference ref) {
		super(document, message);
		this.ref = ref;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8661202074567777000L;

}
