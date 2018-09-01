package com.github.thorbenkuck.powerfx.exceptions;

public class PowerFXException extends Exception {

	public PowerFXException() {
	}

	public PowerFXException(String message) {
		super(message);
	}

	public PowerFXException(String message, Throwable cause) {
		super(message, cause);
	}

	public PowerFXException(Throwable cause) {
		super(cause);
	}

	public PowerFXException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
