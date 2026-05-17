package com.byunyourim.traderbot.infra.kis;

import java.time.Duration;
import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KisAuthService {

	static final String TOKEN_KEY = "kis:access-token";
	static final String APPROVAL_KEY = "kis:approval-key";

	private final KisProperties properties;
	private final StringRedisTemplate redisTemplate;
	private final WebClient kisWebClient;

	public String getAccessToken() {
		String cached = redisTemplate.opsForValue().get(TOKEN_KEY);
		if (cached != null) {
			return cached;
		}
		return fetchAndCache();
	}

	public String getApprovalKey() {
		String cached = redisTemplate.opsForValue().get(APPROVAL_KEY);
		if (cached != null) {
			return cached;
		}
		return fetchApprovalKey();
	}

	@SuppressWarnings("unchecked")
	private String fetchApprovalKey() {
		Map<String, Object> response = kisWebClient.post()
				.uri("/oauth2/Approval")
				.bodyValue(Map.of("grant_type", "client_credentials", "appkey", properties.appKey(), "secretkey",
						properties.appSecret()))
				.retrieve()
				.bodyToMono(Map.class)
				.block();

		if (response == null || !response.containsKey("approval_key")) {
			throw new IllegalStateException("KIS WebSocket approval key 발급 실패");
		}

		String key = (String) response.get("approval_key");
		// approval key TTL: 1일
		redisTemplate.opsForValue().set(APPROVAL_KEY, key, Duration.ofHours(23));
		log.info("KIS WebSocket approval key 발급 완료");
		return key;
	}

	@SuppressWarnings("unchecked")
	private String fetchAndCache() {
		Map<String, Object> response = kisWebClient
				.post().uri("/oauth2/tokenP").bodyValue(Map.of("grant_type", "client_credentials", "appkey",
						properties.appKey(), "appsecret", properties.appSecret()))
				.retrieve().bodyToMono(Map.class).block();

		if (response == null || !response.containsKey("access_token")) {
			throw new IllegalStateException("KIS 토큰 발급 실패");
		}

		String token = (String) response.get("access_token");
		int expiresIn = (int) response.get("expires_in");

		redisTemplate.opsForValue().set(TOKEN_KEY, token, Duration.ofSeconds(expiresIn - 60));
		log.info("KIS 액세스 토큰 발급 완료 (TTL: {}s)", expiresIn - 60);

		return token;
	}
}
