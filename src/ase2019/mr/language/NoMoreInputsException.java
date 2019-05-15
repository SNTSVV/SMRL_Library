package ase2019.mr.language;

public class NoMoreInputsException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 123456L;

	public NoMoreInputsException(String message) {
		super(message);
	}

	public NoMoreInputsException(String message, Throwable throwable) {
		super(message, throwable);
	}
}