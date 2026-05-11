package com.byunyourim.traderbot.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 정기 작업 예시. 한국 주식시장 영업시간(09:00~15:30)을 가정한 cron을 사용.
 * KST 기준이므로 application.yml에서 timezone을 맞춰서 사용하거나 zone을 명시한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MarketDataScheduler {

    @Scheduled(cron = "0 0/1 9-15 * * MON-FRI", zone = "Asia/Seoul")
    public void pollOnceAMinute() {
        log.debug("scheduled poll tick");
        // TODO: 잔고 동기화 / 시세 스냅샷 저장 등
    }
}
