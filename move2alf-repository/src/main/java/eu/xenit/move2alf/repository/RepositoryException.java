package eu.xenit.move2alf.repository;

/**
 * Thrown when the repository can not execute the request.
 * 
 */
public class RepositoryException extends Exception {
	private static final long serialVersionUID = 1;

	public RepositoryException(String message) {
		super(message);
	}

	public RepositoryException(String message, Throwable exception) {
		super(message, exception);
	}
}
