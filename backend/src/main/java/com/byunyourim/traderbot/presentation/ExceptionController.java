package com.byunyourim.traderbot.presentation;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.byunyourim.traderbot.application.ApplicationException;
import com.byunyourim.traderbot.application.ErrorStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ApplicationException.class)
	ResponseEntity<Object> handleApplicationException(ApplicationException ex, WebRequest request) {
		log.warn("ApplicationException [{}] {}", ex.getCode(), ex.getMessage(), ex);
		ErrorResponse body = ErrorResponse.of(ex);
		return handleExceptionInternal(ex, body, new HttpHeaders(), ex.getHttpStatus(), request);
	}

	@ExceptionHandler(RuntimeException.class)
	ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
		log.error("처리되지 않은 예외", ex);
		ErrorStatus status = ErrorStatus.INTERNAL_SERVER_ERROR;
		ErrorResponse body = ErrorResponse.of(status);
		return handleExceptionInternal(ex, body, new HttpHeaders(), status.getHttpStatus(), request);
	}
}
