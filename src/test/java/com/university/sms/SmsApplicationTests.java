package com.university.sms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test: verifies the full Spring application context (controllers,
 * services, repositories, exception handler) wires together correctly.
 * Runs against the in-memory H2 "demo" profile so it needs no external DB.
 */
@SpringBootTest
@ActiveProfiles("demo")
class SmsApplicationTests {

    @Test
    void contextLoads() {
        // If the application context fails to start, this test fails.
    }
}
