package com.byunyourim.traderbot.domain.market;

import java.util.List;

public interface MarketQueryPort {

	MarketQuote getQuote(String symbol);

	List<Candle> getCandles(String symbol, String interval);
}
