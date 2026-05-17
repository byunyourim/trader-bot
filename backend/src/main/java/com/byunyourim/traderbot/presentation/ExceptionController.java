package com.byunyourim.traderbot.presentation;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.byunyourim.traderbot.application.ApplicationException;
import com.byunyourim.traderbot.application.ErrorStatus;
import com.byunyourim.traderbot.infra.kis.KisResponseException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ApplicationException.class)
	ResponseEntity<Object> handleApplicationException(ApplicationException ex, WebRequest request) {
		ErrorResponse body = ErrorResponse.of(ex.getError().name(), ex.getErrorMessage());
		return handleExceptionInternal(ex, body, new HttpHeaders(), ex.getHttpStatus(), request);
	}

	@ExceptionHandler(KisResponseException.class)
	ResponseEntity<Object> handleKisException(KisResponseException ex, WebRequest request) {
		log.error("KIS API 오류: {}", ex.getMessage());
		ErrorResponse body = ErrorResponse.of(ErrorStatus.KIS_API_ERROR.name(), ex.getMessage());
		return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_GATEWAY, request);
	}

	@ExceptionHandler(RuntimeException.class)
	ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
		log.error("처리되지 않은 예외", ex);
		ErrorResponse body = ErrorResponse.of(ErrorStatus.INTERNAL_SERVER_ERROR);
		return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatusCode statusCode, WebRequest request) {
		Object responseBody = body != null ? body : ErrorResponse.of("ERROR", ex.getMessage());
		return super.handleExceptionInternal(ex, responseBody, headers, statusCode, request);
	}
}
