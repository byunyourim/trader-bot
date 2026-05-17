package com.byunyourim.traderbot.infra.kis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kis")
public record KisProperties(String baseUrl, String wsUrl, String appKey, String appSecret, String accountNo,
		String accountProductCode, boolean paperTrading) {
}
