package com.byunyourim.traderbot.presentation.account;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(AccountController.PREFIX)
@RestController
@RequiredArgsConstructor
public class AccountController {

    static final String PREFIX = "/v1/accounts/balance";

    @GetMapping
    void getBalance() {
        // TODO: 잔고 조회 구현
    }
}
