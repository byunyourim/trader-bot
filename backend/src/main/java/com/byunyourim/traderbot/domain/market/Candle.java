package com.byunyourim.traderbot.domain.market;

import java.math.BigDecimal;

// timestamp: Unix epoch seconds (lightweight-charts 호환)
public record Candle(long timestamp, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, long volume) {
}
