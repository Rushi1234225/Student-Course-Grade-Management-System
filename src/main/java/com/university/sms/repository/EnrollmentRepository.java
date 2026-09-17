package com.university.sms.repository;

import com.university.sms.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    Optional<Enrollment> findByStudentIdAndCourseIdAndSemester(Long studentId, Long courseId, String semester);

    boolean existsByStudentIdAndCourseIdAndSemester(Long studentId, Long courseId, String semester);

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.grade <> com.university.sms.model.Grade.NOT_GRADED")
    List<Enrollment> findGradedEnrollmentsByStudent(@Param("studentId") Long studentId);

    // Note: GPA/average-grade math is done in the service layer in Java
    // (not in JPQL) because Grade is stored as an enum, not a numeric column.
}
