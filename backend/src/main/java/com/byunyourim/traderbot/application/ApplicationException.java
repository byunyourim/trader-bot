package com.byunyourim.traderbot.application;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

	private final ErrorStatus error;
	private final Map<String, Object> details = new LinkedHashMap<>();

	public ApplicationException(ErrorStatus error) {
		super(error.getMessage());
		this.error = error;
	}

	public ApplicationException(ErrorStatus error, Throwable cause) {
		super(error.getMessage(), cause);
		this.error = error;
	}

	/**
	 * 디버깅용 컨텍스트를 details 맵에 추가한다.
	 *
	 * <p><b>주의:</b> 여기 담은 값은 ErrorResponse에 그대로 직렬화되어 클라이언트 응답에 노출된다.
	 * 계좌번호 원문, 잔액, 인증 토큰 등 민감 정보는 절대 담지 말 것. (마스킹 후 담거나, 식별용 키만 담을 것)
	 */
	public ApplicationException withDetail(String key, Object value) {
		this.details.put(key, value);
		return this;
	}

	public HttpStatus getHttpStatus() {
		return error.getHttpStatus();
	}

	public String getCode() {
		return error.getCode();
	}

	public Map<String, Object> getDetails() {
		return Collections.unmodifiableMap(details);
	}
}
