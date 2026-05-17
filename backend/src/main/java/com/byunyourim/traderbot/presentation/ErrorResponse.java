package com.byunyourim.traderbot.presentation;

import java.util.Map;

import com.byunyourim.traderbot.application.ApplicationException;
import com.byunyourim.traderbot.application.ErrorStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String code, String error, String message, Map<String, Object> details) {

	public static ErrorResponse of(ApplicationException ex) {
		Map<String, Object> d = ex.getDetails().isEmpty() ? null : ex.getDetails();
		return new ErrorResponse(ex.getCode(), ex.getError().name(), ex.getMessage(), d);
	}

	public static ErrorResponse of(ErrorStatus status) {
		return new ErrorResponse(status.getCode(), status.name(), status.getMessage(), null);
	}

	public static ErrorResponse of(ErrorStatus status, String message, Map<String, Object> details) {
		Map<String, Object> d = (details == null || details.isEmpty()) ? null : details;
		return new ErrorResponse(status.getCode(), status.name(), message, d);
	}
}
