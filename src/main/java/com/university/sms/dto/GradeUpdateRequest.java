package com.university.sms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeUpdateRequest {
    @NotBlank(message = "grade is required (e.g. A, B_PLUS, C_MINUS, F, NOT_GRADED)")
    private String grade;
}
