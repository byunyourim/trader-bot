package com.byunyourim.traderbot.presentation.account;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
class AccountBalanceResponse {

	@Schema(description = "예수금", example = "1000000")
	BigDecimal deposit;

	@Schema(description = "총평가금액", example = "1750000")
	BigDecimal totalEval;

	@Schema(description = "유가평가금액", example = "750000")
	BigDecimal stockEval;

	@Schema(description = "평가손익합계", example = "50000")
	BigDecimal evalProfitLoss;

	@Schema(description = "보유종목")
	List<Position> positions;

	@Schema(name = "AccountBalanceResponse.Position")
	@FieldDefaults(level = AccessLevel.PRIVATE)
	static @Getter @Setter class Position {

		@Schema(description = "종목코드", example = "005930")
		String ticker;

		@Schema(description = "종목명", example = "삼성전자")
		String name;

		@Schema(description = "보유수량", example = "10")
		long holdingQty;

		@Schema(description = "매입평균가", example = "70000.00")
		BigDecimal avgPrice;

		@Schema(description = "현재가", example = "75000")
		BigDecimal currentPrice;

		@Schema(description = "평가금액", example = "750000")
		BigDecimal evalAmount;

		@Schema(description = "평가손익", example = "50000")
		BigDecimal evalProfitLoss;

		@Schema(description = "평가손익률(%)", example = "7.14")
		BigDecimal evalProfitLossRate;
	}
}
