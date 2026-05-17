package com.byunyourim.traderbot.infra.kis;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

import com.byunyourim.traderbot.domain.market.event.MarketTickEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Sinks;

import java.math.BigDecimal;
import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class KisWebSocketClient {

	private final KisProperties properties;
	private final KisAuthService authService;
	private final ApplicationEventPublisher eventPublisher;
	private final ObjectMapper objectMapper;

	private final Set<String> subscribedSymbols = ConcurrentHashMap.newKeySet();
	private final Sinks.Many<String> sendSink = Sinks.many().multicast().onBackpressureBuffer();
	private final AtomicBoolean connected = new AtomicBoolean(false);

	@PostConstruct
	public void connect() {
		String approvalKey = authService.getApprovalKey();
		ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();

		client.execute(URI.create(properties.wsUrl()), session -> {
			connected.set(true);
			log.info("KIS WebSocket 연결 완료");

			// 대기 중인 구독 전송
			subscribedSymbols.forEach(symbol -> sendSubscribe(symbol, approvalKey));

			reactor.core.publisher.Mono<Void> send = session.send(
					sendSink.asFlux().map(session::textMessage));

			reactor.core.publisher.Mono<Void> receive = session.receive()
					.map(WebSocketMessage::getPayloadAsText)
					.doOnNext(this::handleMessage)
					.then();

			return reactor.core.publisher.Mono.zip(send, receive).then();
		}).doOnError(e -> {
			connected.set(false);
			log.error("KIS WebSocket 오류: {}", e.getMessage());
		}).subscribe();
	}

	public void subscribe(String symbol) {
		if (subscribedSymbols.add(symbol) && connected.get()) {
			sendSubscribe(symbol, authService.getApprovalKey());
		}
	}

	private void sendSubscribe(String symbol, String approvalKey) {
		try {
			Map<String, Object> message = Map.of(
					"header", Map.of("approval_key", approvalKey, "custtype", "P", "tr_type", "1", "content-type",
							"utf-8"),
					"body", Map.of("input", Map.of("tr_id", "H0STCNT0", "tr_key", symbol)));
			sendSink.tryEmitNext(objectMapper.writeValueAsString(message));
		} catch (JsonProcessingException e) {
			log.error("KIS WS 구독 메시지 직렬화 실패: {}", e.getMessage());
		}
	}

	// KIS WS 메시지 형식: {type}|{trId}|{count}|{data}
	// data 필드(^ 구분): [0]=종목코드, [2]=현재가, [4]=전일대비, [8]=등락률, [12]=체결거래량
	private void handleMessage(String raw) {
		if (!raw.contains("|")) {
			return; // PINGPONG 또는 응답 메시지
		}

		String[] parts = raw.split("\\|", 4);
		if (parts.length < 4 || !"H0STCNT0".equals(parts[1])) {
			return;
		}

		String[] fields = parts[3].split("\\^");
		if (fields.length < 13) {
			return;
		}

		try {
			String symbol = fields[0];
			BigDecimal price = new BigDecimal(fields[2]);
			BigDecimal change = new BigDecimal(fields[4]);
			BigDecimal changeRate = new BigDecimal(fields[8]);
			long volume = Long.parseLong(fields[12]);
			eventPublisher.publishEvent(new MarketTickEvent(symbol, price, change, changeRate, volume));
		} catch (Exception e) {
			log.warn("KIS WS 메시지 파싱 실패: {}", e.getMessage());
		}
	}
}
