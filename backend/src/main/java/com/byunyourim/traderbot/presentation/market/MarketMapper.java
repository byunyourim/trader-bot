package com.byunyourim.traderbot.presentation.market;

import java.util.List;

import com.byunyourim.traderbot.domain.market.Candle;
import com.byunyourim.traderbot.domain.market.MarketQuote;

class MarketMapper {

	static MarketQuoteResponse toQuoteResponse(MarketQuote quote) {
		MarketQuoteResponse res = new MarketQuoteResponse();
		res.setSymbol(quote.symbol());
		res.setPrice(quote.price());
		res.setChange(quote.change());
		res.setChangeRate(quote.changeRate());
		res.setVolume(quote.volume());
		res.setPrevClose(quote.prevClose());
		return res;
	}

	static CandlesResponse toCandlesResponse(String symbol, List<Candle> candles) {
		CandlesResponse res = new CandlesResponse();
		res.setSymbol(symbol);
		res.setCandles(candles.stream().map(MarketMapper::toCandle).toList());
		return res;
	}

	private static CandlesResponse.Candle toCandle(Candle candle) {
		CandlesResponse.Candle c = new CandlesResponse.Candle();
		c.setTimestamp(candle.timestamp());
		c.setOpen(candle.open());
		c.setHigh(candle.high());
		c.setLow(candle.low());
		c.setClose(candle.close());
		c.setVolume(candle.volume());
		return c;
	}
}
