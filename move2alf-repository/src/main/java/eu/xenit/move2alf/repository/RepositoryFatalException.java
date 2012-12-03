package eu.xenit.move2alf.repository;

/**
 * Thrown when the repository interface encounters an abnormal condition. 
 * This is defined as a runtime exception and it is advisable to
 * stop the program and investigate what exectly is going on.
 */
public class RepositoryFatalException extends RuntimeException {
	private static final long serialVersionUID = 1;

	public RepositoryFatalException(String message) {
		super(message);
	}

	public RepositoryFatalException(String message, Throwable exception) {
		super(message, exception);
	}
}
