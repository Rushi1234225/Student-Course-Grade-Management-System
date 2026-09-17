package com.university.sms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Lightweight health endpoint used to confirm the backend and database
 * connection are up (useful when demonstrating the app / for the video).
 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "student-management-system");
    }
}
