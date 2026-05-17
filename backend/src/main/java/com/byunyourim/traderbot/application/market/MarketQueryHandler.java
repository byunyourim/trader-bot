package com.byunyourim.traderbot.application.market;

import java.util.List;

import org.springframework.stereotype.Component;

import com.byunyourim.traderbot.domain.market.Candle;
import com.byunyourim.traderbot.domain.market.MarketQuote;
import com.byunyourim.traderbot.domain.market.MarketQueryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MarketQueryHandler {

	private final MarketQueryPort marketQueryPort;

	public MarketQuote getQuote(String symbol) {
		return marketQueryPort.getQuote(symbol);
	}

	public List<Candle> getCandles(String symbol, String interval) {
		return marketQueryPort.getCandles(symbol, interval);
	}
}
