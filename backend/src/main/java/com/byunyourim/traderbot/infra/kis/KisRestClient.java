package com.byunyourim.traderbot.infra.kis;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KisRestClient {

	private final WebClient kisWebClient;
	private final KisAuthService authService;
	private final KisProperties properties;

	@SuppressWarnings("unchecked")
	public Map<String, Object> get(String trId, Function<UriBuilder, URI> uriSpec) {
		Map<String, Object> response = kisWebClient.get().uri(uriSpec)
				.header("authorization", "Bearer " + authService.getAccessToken()).header("appkey", properties.appKey())
				.header("appsecret", properties.appSecret()).header("tr_id", resolveRealTrId(trId))
				.header("custtype", "P").retrieve().bodyToMono(Map.class).block();

		if (response == null) {
			throw new IllegalStateException("KIS API 응답 없음: " + trId);
		}

		String rtCd = (String) response.get("rt_cd");
		if (!"0".equals(rtCd)) {
			throw new KisResponseException((String) response.get("msg_cd"), (String) response.get("msg1"));
		}

		return response;
	}

	// 모의투자는 tr_id 접두사 V, 실전은 T
	private String resolveRealTrId(String trId) {
		if (properties.paperTrading()) {
			return trId.startsWith("T") ? "V" + trId.substring(1) : trId;
		}
		return trId;
	}
}
