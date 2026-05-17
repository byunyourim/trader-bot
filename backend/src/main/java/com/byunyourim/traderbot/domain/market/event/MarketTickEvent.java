package com.byunyourim.traderbot.domain.market.event;

import java.math.BigDecimal;

public record MarketTickEvent(String symbol, BigDecimal price, BigDecimal change, BigDecimal changeRate, long volume) {
}
