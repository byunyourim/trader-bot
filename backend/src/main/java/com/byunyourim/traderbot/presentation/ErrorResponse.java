package com.byunyourim.traderbot.presentation;

import com.byunyourim.traderbot.application.ErrorStatus;

public record ErrorResponse(String error, String message) {
	public static ErrorResponse of(ErrorStatus status) {
		return new ErrorResponse(status.name(), status.getMessage());
	}

	public static ErrorResponse of(String error, String message) {
		return new ErrorResponse(error, message);
	}
}
