package com.byunyourim.traderbot.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ErrorStatusTest {

	@Test
	void KIS_API_ERROR_는_K001_코드와_BAD_GATEWAY_를_가진다() {
		assertThat(ErrorStatus.KIS_API_ERROR.getCode()).isEqualTo("K001");
		assertThat(ErrorStatus.KIS_API_ERROR.getMessage()).isEqualTo("KIS API 오류");
		assertThat(ErrorStatus.KIS_API_ERROR.getHttpStatus()).isEqualTo(HttpStatus.BAD_GATEWAY);
	}

	@Test
	void ACCOUNT_QUERY_FAILED_는_A001_코드와_BAD_GATEWAY_를_가진다() {
		assertThat(ErrorStatus.ACCOUNT_QUERY_FAILED.getCode()).isEqualTo("A001");
		assertThat(ErrorStatus.ACCOUNT_QUERY_FAILED.getMessage()).isEqualTo("계좌 조회 실패");
		assertThat(ErrorStatus.ACCOUNT_QUERY_FAILED.getHttpStatus()).isEqualTo(HttpStatus.BAD_GATEWAY);
	}

	@Test
	void INTERNAL_SERVER_ERROR_는_C001_코드와_500_을_가진다() {
		assertThat(ErrorStatus.INTERNAL_SERVER_ERROR.getCode()).isEqualTo("C001");
		assertThat(ErrorStatus.INTERNAL_SERVER_ERROR.getMessage()).isEqualTo("서버 오류");
		assertThat(ErrorStatus.INTERNAL_SERVER_ERROR.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Test
	void 모든_ErrorStatus_의_코드는_고유하다() {
		long distinctCodes = java.util.Arrays.stream(ErrorStatus.values())
		                                     .map(ErrorStatus::getCode)
		                                     .distinct()
		                                     .count();
		assertThat(distinctCodes).isEqualTo(ErrorStatus.values().length);
	}
}
