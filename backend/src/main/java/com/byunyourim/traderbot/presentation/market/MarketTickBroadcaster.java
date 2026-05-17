package com.byunyourim.traderbot.presentation.market;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.byunyourim.traderbot.domain.market.event.MarketTickEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
class MarketTickBroadcaster {

	private final Map<String, List<SseEmitter>> subscribers = new ConcurrentHashMap<>();

	SseEmitter subscribe(String symbol) {
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		subscribers.computeIfAbsent(symbol, k -> new CopyOnWriteArrayList<>()).add(emitter);

		emitter.onCompletion(() -> remove(symbol, emitter));
		emitter.onTimeout(() -> remove(symbol, emitter));
		emitter.onError(e -> remove(symbol, emitter));

		return emitter;
	}

	@EventListener
	public void onTick(MarketTickEvent event) {
		List<SseEmitter> emitters = subscribers.get(event.symbol());
		if (emitters == null || emitters.isEmpty()) {
			return;
		}

		String data = String.format("{\"symbol\":\"%s\",\"price\":%s,\"change\":%s,\"changeRate\":%s,\"volume\":%d}",
				event.symbol(), event.price(), event.change(), event.changeRate(), event.volume());

		emitters.removeIf(emitter -> {
			try {
				emitter.send(SseEmitter.event().data(data));
				return false;
			} catch (IOException e) {
				return true; // 전송 실패 시 제거
			}
		});
	}

	private void remove(String symbol, SseEmitter emitter) {
		List<SseEmitter> emitters = subscribers.get(symbol);
		if (emitters != null) {
			emitters.remove(emitter);
		}
	}
}
