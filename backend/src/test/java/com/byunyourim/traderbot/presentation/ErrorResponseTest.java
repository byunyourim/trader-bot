package com.byunyourim.traderbot.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.byunyourim.traderbot.application.ApplicationException;
import com.byunyourim.traderbot.application.ErrorStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

class ErrorResponseTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void of_ApplicationException_은_code_error_message_를_매핑한다() {
		ApplicationException ex = new ApplicationException(ErrorStatus.KIS_API_ERROR);

		ErrorResponse response = ErrorResponse.of(ex);

		assertThat(response.code()).isEqualTo("K001");
		assertThat(response.error()).isEqualTo("KIS_API_ERROR");
		assertThat(response.message()).isEqualTo("KIS API 오류");
		assertThat(response.details()).isNull();
	}

	@Test
	void of_ApplicationException_은_details_가_있으면_그대로_담는다() {
		ApplicationException ex = new ApplicationException(ErrorStatus.ACCOUNT_QUERY_FAILED)
		                                                                                    .withDetail("account", "1234")
		                                                                                    .withDetail("retry", 3);

		ErrorResponse response = ErrorResponse.of(ex);

		assertThat(response.details()).containsEntry("account", "1234").containsEntry("retry", 3);
	}

	@Test
	void of_ApplicationException_은_details_가_비어있으면_null_로_담는다() {
		ApplicationException ex = new ApplicationException(ErrorStatus.KIS_API_ERROR);

		ErrorResponse response = ErrorResponse.of(ex);

		assertThat(response.details()).isNull();
	}

	@Test
	void of_ErrorStatus_단일_인자는_status_의_기본_message_를_사용한다() {
		ErrorResponse response = ErrorResponse.of(ErrorStatus.INTERNAL_SERVER_ERROR);

		assertThat(response.code()).isEqualTo("C001");
		assertThat(response.error()).isEqualTo("INTERNAL_SERVER_ERROR");
		assertThat(response.message()).isEqualTo("서버 오류");
		assertThat(response.details()).isNull();
	}

	@Test
	void of_ErrorStatus_message_details_3인자_정상_매핑() {
		Map<String, Object> details = new LinkedHashMap<>();
		details.put("foo", "bar");

		ErrorResponse response = ErrorResponse.of(ErrorStatus.KIS_API_ERROR, "custom msg", details);

		assertThat(response.code()).isEqualTo("K001");
		assertThat(response.error()).isEqualTo("KIS_API_ERROR");
		assertThat(response.message()).isEqualTo("custom msg");
		assertThat(response.details()).containsEntry("foo", "bar");
	}

	@Test
	void of_ErrorStatus_3인자_details_가_null_이면_null_로_담는다() {
		ErrorResponse response = ErrorResponse.of(ErrorStatus.KIS_API_ERROR, "msg", null);

		assertThat(response.details()).isNull();
	}

	@Test
	void of_ErrorStatus_3인자_details_가_빈_맵이면_null_로_담는다() {
		ErrorResponse response = ErrorResponse.of(ErrorStatus.KIS_API_ERROR, "msg", Map.of());

		assertThat(response.details()).isNull();
	}

	@Test
	void Jackson_직렬화_시_null_details_는_JSON_에서_빠진다() throws Exception {
		ErrorResponse response = ErrorResponse.of(ErrorStatus.INTERNAL_SERVER_ERROR);

		String json = objectMapper.writeValueAsString(response);

		assertThat(json).doesNotContain("details");
		assertThat(json).contains("\"code\":\"C001\"");
		assertThat(json).contains("\"error\":\"INTERNAL_SERVER_ERROR\"");
		assertThat(json).contains("\"message\":\"서버 오류\"");
	}

	@Test
	void Jackson_직렬화_시_details_가_있으면_JSON_에_포함된다() throws Exception {
		ApplicationException ex = new ApplicationException(ErrorStatus.KIS_API_ERROR).withDetail("k", "v");

		String json = objectMapper.writeValueAsString(ErrorResponse.of(ex));

		assertThat(json).contains("\"details\":{\"k\":\"v\"}");
	}
}
