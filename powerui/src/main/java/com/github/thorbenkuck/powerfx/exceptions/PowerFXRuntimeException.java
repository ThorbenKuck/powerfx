package com.github.thorbenkuck.powerfx.exceptions;

public class PowerFXRuntimeException extends RuntimeException {

	public PowerFXRuntimeException() {
	}

	public PowerFXRuntimeException(String message) {
		super(message);
	}

	public PowerFXRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public PowerFXRuntimeException(Throwable cause) {
		super(cause);
	}

	public PowerFXRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
