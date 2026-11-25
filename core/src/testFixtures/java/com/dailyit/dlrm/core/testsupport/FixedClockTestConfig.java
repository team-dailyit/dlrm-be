package com.dailyit.dlrm.core.testsupport;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/** 통합 테스트용 고정 시계. 현재 시각을 2026-10-13(화) 15:00 KST로 고정한다. */
@TestConfiguration(proxyBeanMethods = false)
public class FixedClockTestConfig {

    public static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 13, 15, 0);

    @Bean
    @Primary
    Clock fixedClock() {
        ZoneId zone = ZoneId.of("Asia/Seoul");
        return Clock.fixed(NOW.atZone(zone).toInstant(), zone);
    }
}
