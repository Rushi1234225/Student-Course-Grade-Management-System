package com.university.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Student Course & Grade Management System.
 *
 * This application demonstrates a full data-driven Spring Boot system:
 *  - Backend layered architecture (Controller -> Service -> Repository -> Database)
 *  - CRUD operations over a relational database (MySQL / H2)
 *  - RESTful API consumed by a lightweight HTML/JS dashboard
 *  - Centralized exception handling with graceful fallbacks
 */
@SpringBootApplication
public class SmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmsApplication.class, args);
    }
}
