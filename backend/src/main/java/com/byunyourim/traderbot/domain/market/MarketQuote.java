package com.byunyourim.traderbot.domain.market;

import java.math.BigDecimal;

public record MarketQuote(String symbol, BigDecimal price, BigDecimal change, BigDecimal changeRate, long volume,
		BigDecimal prevClose) {
}
