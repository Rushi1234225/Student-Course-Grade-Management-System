package com.university.sms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a course offered by the institution.
 * Uses @Getter/@Setter (not @Data) — see note in Student.java about
 * avoiding recursive equals/hashCode/toString on bidirectional relations.
 */
@Entity
@Table(name = "courses", uniqueConstraints = {
        @UniqueConstraint(columnNames = "course_code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Course code is required")
    @Column(name = "course_code", nullable = false, unique = true, length = 20)
    private String courseCode;

    @NotBlank(message = "Course title is required")
    @Column(nullable = false, length = 150)
    private String title;

    @Min(value = 1, message = "Credit hours must be at least 1")
    @Column(name = "credit_hours", nullable = false)
    private int creditHours;

    @Column(length = 100)
    private String department;

    @Column(length = 500)
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();
}
