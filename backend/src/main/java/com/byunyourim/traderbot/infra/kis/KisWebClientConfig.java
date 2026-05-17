package com.byunyourim.traderbot.infra.kis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class KisWebClientConfig {

	@Bean
	public WebClient kisWebClient(KisProperties properties) {
		return WebClient.builder().baseUrl(properties.baseUrl())
				.defaultHeader("content-type", "application/json; charset=utf-8").build();
	}
}
