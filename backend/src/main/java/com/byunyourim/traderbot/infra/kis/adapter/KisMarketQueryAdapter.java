package com.byunyourim.traderbot.infra.kis.adapter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.byunyourim.traderbot.domain.market.Candle;
import com.byunyourim.traderbot.domain.market.MarketQuote;
import com.byunyourim.traderbot.domain.market.MarketQueryPort;
import com.byunyourim.traderbot.infra.kis.KisRestClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KisMarketQueryAdapter implements MarketQueryPort {

	private static final String QUOTE_TR_ID = "FHKST01010100";
	private static final String CANDLE_TR_ID = "FHKST03010100";
	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

	private final KisRestClient kisRestClient;

	@Override
	@SuppressWarnings("unchecked")
	public MarketQuote getQuote(String symbol) {
		Map<String, Object> response = kisRestClient.get(QUOTE_TR_ID,
				uriBuilder -> uriBuilder.path("/uapi/domestic-stock/v1/quotations/inquire-price")
						.queryParam("FID_COND_MRKT_DIV_CODE", "J")
						.queryParam("FID_INPUT_ISCD", symbol)
						.build());

		Map<String, Object> output = (Map<String, Object>) response.get("output");
		return new MarketQuote(symbol, decimal(output, "stck_prpr"), decimal(output, "prdy_vrss"),
				decimal(output, "prdy_ctrt"), Long.parseLong(str(output, "acml_vol")),
				decimal(output, "stck_clpr"));
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Candle> getCandles(String symbol, String interval) {
		String today = LocalDate.now().format(DATE_FMT);
		String from = LocalDate.now().minusYears(1).format(DATE_FMT);

		Map<String, Object> response = kisRestClient.get(CANDLE_TR_ID,
				uriBuilder -> uriBuilder.path("/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice")
						.queryParam("FID_COND_MRKT_DIV_CODE", "J")
						.queryParam("FID_INPUT_ISCD", symbol)
						.queryParam("FID_INPUT_DATE_1", from)
						.queryParam("FID_INPUT_DATE_2", today)
						.queryParam("FID_PERIOD_DIV_CODE", interval.toUpperCase())
						.queryParam("FID_ORG_ADJ_PRC", "0")
						.build());

		List<Map<String, Object>> output2 = (List<Map<String, Object>>) response.get("output2");
		if (output2 == null) {
			return List.of();
		}

		return output2.stream()
				      .map(this::toCandle)
				      .toList();
	}

	private Candle toCandle(Map<String, Object> row) {
		long timestamp = LocalDate.parse(str(row, "stck_bsop_date"), DATE_FMT)
				                  .atStartOfDay()
				                  .toEpochSecond(ZoneOffset.UTC);
		return new Candle(timestamp, decimal(row, "stck_oprc"), decimal(row, "stck_hgpr"),
				decimal(row, "stck_lwpr"), decimal(row, "stck_clpr"), Long.parseLong(str(row, "acml_vol")));
	}

	private static String str(Map<String, Object> map, String key) {
		Object val = map.get(key);
		return val == null ? "" : val.toString().trim();
	}

	private static BigDecimal decimal(Map<String, Object> map, String key) {
		String val = str(map, key);
		return val.isEmpty() ? BigDecimal.ZERO : new BigDecimal(val);
	}
}
