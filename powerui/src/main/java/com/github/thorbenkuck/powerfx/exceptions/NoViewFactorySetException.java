package com.github.thorbenkuck.powerfx.exceptions;

public class NoViewFactorySetException extends PowerFXRuntimeException {

	public NoViewFactorySetException() {
	}

	public NoViewFactorySetException(String message) {
		super(message);
	}

	public NoViewFactorySetException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoViewFactorySetException(Throwable cause) {
		super(cause);
	}

	public NoViewFactorySetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
