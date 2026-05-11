package com.byunyourim.traderbot.kis;

import com.byunyourim.traderbot.config.KisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 한국투자증권 REST API 호출 래퍼.
 * 잔고 조회 / 주문 / 계좌 조회 등 REST 엔드포인트 추가는 이 클래스에서.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KisRestClient {

    private final WebClient kisWebClient;
    private final KisAuthService authService;
    private final KisProperties properties;

    public Mono<Map> getBalance() {
        return authService.getAccessToken().flatMap(token ->
                kisWebClient.get()
                        .uri(uri -> uri.path("/uapi/domestic-stock/v1/trading/inquire-balance")
                                .queryParam("CANO", properties.accountNo())
                                .queryParam("ACNT_PRDT_CD", properties.accountProductCode())
                                .queryParam("AFHR_FLPR_YN", "N")
                                .queryParam("OFL_YN", "")
                                .queryParam("INQR_DVSN", "02")
                                .queryParam("UNPR_DVSN", "01")
                                .queryParam("FUND_STTL_ICLD_YN", "N")
                                .queryParam("FNCG_AMT_AUTO_RDPT_YN", "N")
                                .queryParam("PRCS_DVSN", "01")
                                .queryParam("CTX_AREA_FK100", "")
                                .queryParam("CTX_AREA_NK100", "")
                                .build())
                        .header("authorization", "Bearer " + token)
                        .header("appkey", properties.appKey())
                        .header("appsecret", properties.appSecret())
                        .header("tr_id", properties.paperTrading() ? "VTTC8434R" : "TTTC8434R")
                        .retrieve()
                        .bodyToMono(Map.class)
        );
    }
}
