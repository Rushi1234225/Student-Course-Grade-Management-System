package com.university.sms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Long id;

    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    private LocalDate dateOfBirth;
    private String major;
    private LocalDate enrollmentDate;

    // Computed, read-only convenience field returned by the API
    private Double gpa;
}
