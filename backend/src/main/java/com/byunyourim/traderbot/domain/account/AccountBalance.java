package com.byunyourim.traderbot.domain.account;

import java.math.BigDecimal;
import java.util.List;

public record AccountBalance(BigDecimal deposit, BigDecimal totalEval, BigDecimal stockEval, BigDecimal evalProfitLoss,
		List<AccountPosition> positions) {
}
