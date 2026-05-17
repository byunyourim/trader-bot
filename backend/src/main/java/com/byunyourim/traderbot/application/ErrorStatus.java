package com.byunyourim.traderbot.application;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorStatus {

	KIS_API_ERROR("K001", "KIS API 오류", HttpStatus.BAD_GATEWAY),
	ACCOUNT_QUERY_FAILED("A001", "계좌 조회 실패", HttpStatus.BAD_GATEWAY),
	INTERNAL_SERVER_ERROR("C001", "서버 오류", HttpStatus.INTERNAL_SERVER_ERROR);

	private final String code;
	private final String message;
	private final HttpStatus httpStatus;
}
