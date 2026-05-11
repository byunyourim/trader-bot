package com.byunyourim.traderbot.account;

import com.byunyourim.traderbot.kis.KisRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final KisRestClient kisRestClient;

    @GetMapping("/balance")
    public Mono<Map> getBalance() {
        return kisRestClient.getBalance();
    }
}
