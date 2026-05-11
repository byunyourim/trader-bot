package com.byunyourim.traderbot.kis;

import com.byunyourim.traderbot.config.KisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KisAuthService {

    private static final String TOKEN_CACHE_KEY = "kis:access-token";

    private final WebClient kisWebClient;
    private final KisProperties properties;
    private final ReactiveStringRedisTemplate redisTemplate;

    public Mono<String> getAccessToken() {
        return redisTemplate.opsForValue().get(TOKEN_CACHE_KEY)
                .switchIfEmpty(Mono.defer(this::issueAccessToken));
    }

    private Mono<String> issueAccessToken() {
        Map<String, String> body = Map.of(
                "grant_type", "client_credentials",
                "appkey", properties.appKey(),
                "appsecret", properties.appSecret()
        );

        return kisWebClient.post()
                .uri("/oauth2/tokenP")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(resp -> {
                    String token = (String) resp.get("access_token");
                    Object expiresIn = resp.get("expires_in");
                    long ttl = expiresIn instanceof Number n ? n.longValue() : 86400L;
                    return redisTemplate.opsForValue()
                            .set(TOKEN_CACHE_KEY, token, Duration.ofSeconds(ttl - 60))
                            .thenReturn(token);
                });
    }
}
