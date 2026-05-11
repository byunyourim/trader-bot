package com.byunyourim.traderbot.kis;

import com.byunyourim.traderbot.config.KisProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import java.net.URI;

/**
 * 한국투자증권 WebSocket API 연결.
 * 실시간 시세/체결 데이터 구독은 이 클래스에서 처리한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KisWebSocketClient {

    private final KisProperties properties;
    private final WebSocketClient client = new ReactorNettyWebSocketClient();

    @PostConstruct
    public void init() {
        log.info("KIS WebSocket client initialized. url={}", properties.wsUrl());
        // TODO: 종목 등록(approval_key 발급, subscribe 메시지 전송) 로직 구현
    }

    public void subscribe(String trId, String trKey) {
        URI uri = URI.create(properties.wsUrl());
        log.info("subscribe(trId={}, trKey={}) on {}", trId, trKey, uri);
        // TODO: client.execute(uri, session -> ...) 로 실시간 데이터 구독
    }
}
