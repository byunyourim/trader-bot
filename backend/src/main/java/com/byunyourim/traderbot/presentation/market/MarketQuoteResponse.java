package com.byunyourim.traderbot.presentation.market;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
class MarketQuoteResponse {

	@Schema(description = "종목코드", example = "005930")
	String symbol;

	@Schema(description = "현재가", example = "72400")
	BigDecimal price;

	@Schema(description = "전일 대비", example = "-200")
	BigDecimal change;

	@Schema(description = "등락률(%)", example = "-0.28")
	BigDecimal changeRate;

	@Schema(description = "누적 거래량", example = "12345678")
	long volume;

	@Schema(description = "전일 종가", example = "72600")
	BigDecimal prevClose;
}
