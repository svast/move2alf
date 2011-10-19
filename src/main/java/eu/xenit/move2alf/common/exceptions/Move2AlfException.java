package eu.xenit.move2alf.common.exceptions;

public class Move2AlfException extends RuntimeException {

	public Move2AlfException() {
		super();
	}
	
	public Move2AlfException(String message) {
		super(message);
	}
	
	public Move2AlfException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public Move2AlfException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = -2779171115159650333L;

}
