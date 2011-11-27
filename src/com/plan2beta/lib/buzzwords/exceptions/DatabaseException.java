package com.plan2beta.lib.buzzwords.exceptions;

public class DatabaseException extends Exception {

	/**
     * 
     */
	private static final long serialVersionUID = 511937409103808716L;

	public DatabaseException() {}

	public DatabaseException(String message) {
		super(message);
	}

	public DatabaseException(Throwable cause) {
		super(cause);
	}

	public DatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

}
