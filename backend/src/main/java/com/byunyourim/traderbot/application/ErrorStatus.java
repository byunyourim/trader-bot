package com.byunyourim.traderbot.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorStatus {

	KIS_API_ERROR("KIS API 오류"), INTERNAL_SERVER_ERROR("서버 오류");

	private final String message;
}
