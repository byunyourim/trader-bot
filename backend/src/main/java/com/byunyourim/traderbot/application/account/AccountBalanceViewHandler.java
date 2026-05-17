package com.byunyourim.traderbot.application.account;

import java.util.List;

import org.springframework.stereotype.Component;

import com.byunyourim.traderbot.domain.account.AccountBalance;
import com.byunyourim.traderbot.domain.account.AccountPosition;
import com.byunyourim.traderbot.domain.account.AccountQueryPort;
import com.byunyourim.traderbot.infra.kis.KisProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountBalanceViewHandler {

	private final AccountQueryPort accountQueryPort;
	private final KisProperties kisProperties;

	public AccountBalance getBalance() {
		return accountQueryPort.getBalance(kisProperties.accountNo(), kisProperties.accountProductCode());
	}

	public List<AccountPosition> getPositions() {
		return getBalance().positions();
	}
}
