package eu.xenit.move2alf.repository;

/**
 * Thrown when there is a connectivity problem with the repository.
 * 
 */
public class RepositoryAccessException extends Exception {
	private static final long serialVersionUID = 1;

	public RepositoryAccessException(String message) {
		super(message);
	}

	public RepositoryAccessException(String message, Throwable exception) {
		super(message, exception);
	}
}
