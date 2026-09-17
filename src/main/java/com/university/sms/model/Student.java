package com.university.sms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a student record.
 * A Student has a many-to-many relationship with Course, resolved through
 * the Enrollment join entity (which carries the grade for that pairing).
 *
 * NOTE: uses @Getter/@Setter rather than @Data on purpose. @Data would
 * generate equals()/hashCode()/toString() that walk into the enrollments
 * collection, which walks back into Student/Course — an infinite
 * recursion trap on bidirectional JPA relationships.
 */
@Entity
@Table(name = "students", uniqueConstraints = {
        @UniqueConstraint(columnNames = "registration_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Registration number is required")
    @Column(name = "registration_number", nullable = false, unique = true, length = 20)
    private String registrationNumber;

    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false, length = 60)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false, length = 60)
    private String lastName;

    @Email(message = "Email must be a valid email address")
    @NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 100)
    private String major;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate = LocalDate.now();

    @JsonIgnore
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();
}
