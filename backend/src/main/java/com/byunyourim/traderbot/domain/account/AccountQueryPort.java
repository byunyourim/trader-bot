package com.byunyourim.traderbot.domain.account;

public interface AccountQueryPort {
	AccountBalance getBalance(String accountNo, String accountProductCode);
}
