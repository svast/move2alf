package eu.xenit.move2alf.repository;

import java.util.List;

public class PartialUploadFailureException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5998434617388814791L;
	private final List<IllegalDocumentException> exceptions;

	public PartialUploadFailureException(
			List<IllegalDocumentException> exceptions) {
		this.exceptions = exceptions;
	}
	
	public List<IllegalDocumentException> getExceptions(){
		return this.exceptions;
	}

}
