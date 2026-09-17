package com.university.sms.repository;

import com.university.sms.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRegistrationNumber(String registrationNumber);
    Optional<Student> findByEmail(String email);
    boolean existsByRegistrationNumber(String registrationNumber);
    boolean existsByEmail(String email);
}
