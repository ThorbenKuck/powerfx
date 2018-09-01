package com.github.thorbenkuck.powerfx.exceptions;

public class NoPresenterFactorySetException extends PowerFXRuntimeException {
	public NoPresenterFactorySetException() {
	}

	public NoPresenterFactorySetException(String message) {
		super(message);
	}

	public NoPresenterFactorySetException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoPresenterFactorySetException(Throwable cause) {
		super(cause);
	}

	public NoPresenterFactorySetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
