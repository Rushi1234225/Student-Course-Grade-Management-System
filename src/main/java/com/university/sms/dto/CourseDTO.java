package com.university.sms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long id;

    @NotBlank(message = "Course code is required")
    private String courseCode;

    @NotBlank(message = "Course title is required")
    private String title;

    @Min(value = 1, message = "Credit hours must be at least 1")
    private int creditHours;

    private String department;
    private String description;
    private Integer enrolledCount;
}
