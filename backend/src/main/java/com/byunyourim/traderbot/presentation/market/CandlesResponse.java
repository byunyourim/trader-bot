package com.byunyourim.traderbot.presentation.market;

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
class CandlesResponse {

	@Schema(description = "종목코드", example = "005930")
	String symbol;

	@Schema(description = "캔들 목록")
	List<Candle> candles;

	@Schema(name = "CandlesResponse.Candle")
	@FieldDefaults(level = AccessLevel.PRIVATE)
	static @Getter @Setter class Candle {

		@Schema(description = "Unix epoch seconds", example = "1700000000")
		long timestamp;

		@Schema(description = "시가", example = "72000")
		BigDecimal open;

		@Schema(description = "고가", example = "73000")
		BigDecimal high;

		@Schema(description = "저가", example = "71500")
		BigDecimal low;

		@Schema(description = "종가", example = "72400")
		BigDecimal close;

		@Schema(description = "거래량", example = "12345678")
		long volume;
	}
}
