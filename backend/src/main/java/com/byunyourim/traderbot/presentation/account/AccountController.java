package com.byunyourim.traderbot.presentation.account;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.byunyourim.traderbot.application.account.AccountBalanceViewHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "계좌")
@RequestMapping(AccountController.PREFIX)
@RestController
@RequiredArgsConstructor
public class AccountController {

	static final String PREFIX = "/v1/accounts";

	private final AccountBalanceViewHandler viewHandler;

	@Operation(summary = "1. 잔고 스냅샷 조회", description = "예수금·총평가금액·보유종목을 한 번에 조회합니다.")
	@GetMapping("/balance")
	AccountBalanceResponse getBalance() {
		return AccountMapper.toBalanceResponse(viewHandler.getBalance());
	}

	@Operation(summary = "2. 보유종목 리스트 조회", description = "현재 보유 중인 종목 목록을 조회합니다.")
	@GetMapping("/positions")
	AccountPositionsResponse getPositions() {
		return AccountMapper.toPositionsResponse(viewHandler.getPositions());
	}
}
