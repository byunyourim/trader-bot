package com.byunyourim.traderbot.application;

import org.springframework.http.HttpStatus;

public abstract class ApplicationException extends RuntimeException {

	private final ErrorStatus error;

	protected ApplicationException(ErrorStatus error) {
		super();
		this.error = error;
	}

	public ErrorStatus getError() {
		return error;
	}

	@Override
	public String getMessage() {
		return getErrorMessage();
	}

	public abstract HttpStatus getHttpStatus();

	public abstract String getErrorMessage();
}
