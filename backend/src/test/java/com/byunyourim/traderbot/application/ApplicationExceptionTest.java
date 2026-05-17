package com.byunyourim.traderbot.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ApplicationExceptionTest {

	@Test
	void ErrorStatus_단일_생성자는_메시지와_에러를_보존한다() {
		ApplicationException ex = new ApplicationException(ErrorStatus.KIS_API_ERROR);

		assertThat(ex.getError()).isEqualTo(ErrorStatus.KIS_API_ERROR);
		assertThat(ex.getMessage()).isEqualTo("KIS API 오류");
		assertThat(ex.getCode()).isEqualTo("K001");
		assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.BAD_GATEWAY);
		assertThat(ex.getCause()).isNull();
	}

	@Test
	void Throwable_을_받는_생성자는_cause_를_보존한다() {
		RuntimeException cause = new RuntimeException("root cause");

		ApplicationException ex = new ApplicationException(ErrorStatus.ACCOUNT_QUERY_FAILED, cause);

		assertThat(ex.getError()).isEqualTo(ErrorStatus.ACCOUNT_QUERY_FAILED);
		assertThat(ex.getCause()).isSameAs(cause);
		assertThat(ex.getMessage()).isEqualTo("계좌 조회 실패");
		assertThat(ex.getCode()).isEqualTo("A001");
		assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.BAD_GATEWAY);
	}

	@Test
	void 생성_직후_details_는_비어있다() {
		ApplicationException ex = new ApplicationException(ErrorStatus.INTERNAL_SERVER_ERROR);

		assertThat(ex.getDetails()).isEmpty();
	}

	@Test
	void withDetail_은_같은_인스턴스를_반환해_체이닝을_지원한다() {
		ApplicationException ex = new ApplicationException(ErrorStatus.KIS_API_ERROR);

		ApplicationException returned = ex.withDetail("key", "value");

		assertThat(returned).isSameAs(ex);
	}

	@Test
	void withDetail_체이닝_시_입력_순서를_유지한다() {
		ApplicationException ex = new ApplicationException(ErrorStatus.KIS_API_ERROR)
		                                                                              .withDetail("first", 1)
		                                                                              .withDetail("second", 2)
		                                                                              .withDetail("third", 3);

		assertThat(ex.getDetails()).containsExactly(
		    java.util.Map.entry("first", 1),
		    java.util.Map.entry("second", 2),
		    java.util.Map.entry("third", 3));
	}

	@Test
	void 같은_키로_withDetail_두_번_호출_시_덮어쓴다() {
		ApplicationException ex = new ApplicationException(ErrorStatus.KIS_API_ERROR)
		                                                                              .withDetail("k", "v1")
		                                                                              .withDetail("k", "v2");

		assertThat(ex.getDetails()).hasSize(1);
		assertThat(ex.getDetails()).containsEntry("k", "v2");
	}

	@Test
	void getDetails_는_unmodifiableMap_을_반환한다() {
		ApplicationException ex = new ApplicationException(ErrorStatus.KIS_API_ERROR).withDetail("k", "v");

		assertThatThrownBy(() -> ex.getDetails().put("new", "x"))
		                                                        .isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void ApplicationException_은_RuntimeException_을_상속한다() {
		ApplicationException ex = new ApplicationException(ErrorStatus.INTERNAL_SERVER_ERROR);

		assertThat(ex).isInstanceOf(RuntimeException.class);
	}
}
