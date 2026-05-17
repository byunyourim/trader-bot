package com.byunyourim.traderbot.presentation.market;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.byunyourim.traderbot.application.market.MarketQueryHandler;
import com.byunyourim.traderbot.infra.kis.KisWebSocketClient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "시세")
@RequestMapping(MarketController.PREFIX)
@RestController
@RequiredArgsConstructor
public class MarketController {

	static final String PREFIX = "/v1/market";

	private final MarketQueryHandler queryHandler;
	private final KisWebSocketClient kisWebSocketClient;
	private final MarketTickBroadcaster broadcaster;

	@Operation(summary = "1. 현재가 조회", description = "종목의 현재가·전일대비·등락률·거래량을 조회합니다.")
	@GetMapping("/quote/{symbol}")
	MarketQuoteResponse getQuote(@PathVariable String symbol) {
		return MarketMapper.toQuoteResponse(queryHandler.getQuote(symbol));
	}

	@Operation(summary = "2. 캔들 조회", description = "종목의 일봉/주봉/월봉 데이터를 조회합니다. interval: D(일), W(주), M(월)")
	@GetMapping("/candles/{symbol}")
	CandlesResponse getCandles(@PathVariable String symbol,
			@RequestParam(defaultValue = "D") String interval) {
		return MarketMapper.toCandlesResponse(symbol, queryHandler.getCandles(symbol, interval));
	}

	@Operation(summary = "3. 실시간 tick 구독 (SSE)", description = "KIS WebSocket에서 수신한 체결 tick을 SSE로 스트리밍합니다.")
	@GetMapping(value = "/ticks/{symbol}", produces = "text/event-stream")
	SseEmitter streamTicks(@PathVariable String symbol) {
		kisWebSocketClient.subscribe(symbol);
		return broadcaster.subscribe(symbol);
	}
}
