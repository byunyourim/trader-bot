package com.byunyourim.traderbot.domain.account;

import java.math.BigDecimal;

public record AccountPosition(String ticker, String name, long holdingQty, BigDecimal avgPrice, BigDecimal currentPrice,
		BigDecimal evalAmount, BigDecimal evalProfitLoss, BigDecimal evalProfitLossRate) {
}
