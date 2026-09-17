package com.university.sms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Join entity representing a single Student's enrollment in a single Course,
 * along with the grade earned (if any). This models the many-to-many
 * relationship between Student and Course as a first-class entity so that
 * additional attributes (grade, semester, enrolled date) can be tracked.
 * Uses @Getter/@Setter (not @Data) — see note in Student.java about
 * avoiding recursive equals/hashCode/toString on bidirectional relations.
 */
@Entity
@Table(name = "enrollments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "course_id", "semester"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 20)
    private String semester; // e.g. "Fall2026"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Grade grade = Grade.NOT_GRADED;

    @Column(name = "enrolled_on")
    private LocalDate enrolledOn = LocalDate.now();
}
