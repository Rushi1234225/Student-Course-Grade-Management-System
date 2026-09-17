package com.university.sms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {
    private Long id;

    @NotNull(message = "studentId is required")
    private Long studentId;

    @NotNull(message = "courseId is required")
    private Long courseId;

    @NotBlank(message = "semester is required (e.g. Fall2026)")
    private String semester;

    // Sent as a plain string, e.g. "A", "B_PLUS", "NOT_GRADED"
    private String grade;

    private LocalDate enrolledOn;

    // Read-only convenience fields for the response / frontend display
    private String studentName;
    private String courseTitle;
}
