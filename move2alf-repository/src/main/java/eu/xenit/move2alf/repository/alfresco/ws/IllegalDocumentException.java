package eu.xenit.move2alf.repository.alfresco.ws;

public class IllegalDocumentException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8134320820502155651L;
	private Document document;

	public IllegalDocumentException(Document document) {
		this.document = document;
	}

	public Document getDocument() {
		return document;
	}

}
