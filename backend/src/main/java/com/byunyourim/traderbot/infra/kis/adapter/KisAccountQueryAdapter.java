package com.byunyourim.traderbot.infra.kis.adapter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.byunyourim.traderbot.domain.account.AccountBalance;
import com.byunyourim.traderbot.domain.account.AccountPosition;
import com.byunyourim.traderbot.domain.account.AccountQueryPort;
import com.byunyourim.traderbot.infra.kis.KisRestClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KisAccountQueryAdapter implements AccountQueryPort {

	private static final String TR_ID = "TTTC8434R";

	private final KisRestClient kisRestClient;

	@Override
	@SuppressWarnings("unchecked")
	public AccountBalance getBalance(String accountNo, String accountProductCode) {
		Map<String, Object> response = kisRestClient.get(TR_ID,
				uriBuilder -> uriBuilder.path("/uapi/domestic-stock/v1/trading/inquire-balance")
						.queryParam("CANO", accountNo).queryParam("ACNT_PRDT_CD", accountProductCode)
						.queryParam("AFHR_FLPR_YN", "N").queryParam("OFL_YN", "").queryParam("INQR_DVSN", "02")
						.queryParam("UNPR_DVSN", "01").queryParam("FUND_STTL_ICLD_YN", "N")
						.queryParam("FNCG_AMT_AUTO_RDMP_YN", "N").queryParam("PRCS_DVSN", "01")
						.queryParam("CTX_AREA_FK100", "").queryParam("CTX_AREA_NK100", "").build());

		List<Map<String, Object>> output1 = (List<Map<String, Object>>) response.get("output1");
		List<Map<String, Object>> output2 = (List<Map<String, Object>>) response.get("output2");

		List<AccountPosition> positions = output1.stream().map(this::toPosition).toList();

		Map<String, Object> summary = output2.get(0);
		return new AccountBalance(decimal(summary, "dnca_tot_amt"), decimal(summary, "tot_evlu_amt"),
				decimal(summary, "scts_evlu_amt"), decimal(summary, "evlu_pfls_smtl_amt"), positions);
	}

	private AccountPosition toPosition(Map<String, Object> row) {
		return new AccountPosition(str(row, "pdno"), str(row, "prdt_name"), Long.parseLong(str(row, "hldg_qty")),
				decimal(row, "pchs_avg_pric"), decimal(row, "prpr"), decimal(row, "evlu_amt"),
				decimal(row, "evlu_pfls_amt"), decimal(row, "evlu_pfls_rt"));
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
