package com.axolotl.mancala;

/**
 * A common exception type to be raised
 */
public class MancalaException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructs a new instance of the MancalaException class
	 */
	public MancalaException() {
		super();
	}
	
	/**
	 * Constructs a new instance of the MancalaException class
	 * 
	 * @param message The exception message
	 */
	public MancalaException(String message) {
		super(message);
	}
	
	/**
	 * Constructs a new instance of the MancalaException class
	 * 
	 * @param message The exception message
	 * 
	 * @param innerException The inner exception
	 */
	public MancalaException(String message, Throwable innerException) {
		super(message, innerException);
	}
}
