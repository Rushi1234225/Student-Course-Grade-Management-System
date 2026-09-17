package com.university.sms.controller;

import com.university.sms.dto.EnrollmentDTO;
import com.university.sms.dto.GradeUpdateRequest;
import com.university.sms.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<EnrollmentDTO> create(@Valid @RequestBody EnrollmentDTO dto) {
        EnrollmentDTO created = enrollmentService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentDTO>> getAll() {
        return ResponseEntity.ok(enrollmentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.getById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentDTO>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.getByStudent(studentId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentDTO>> getByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getByCourse(courseId));
    }

    @PatchMapping("/{id}/grade")
    public ResponseEntity<EnrollmentDTO> updateGrade(@PathVariable Long id, @Valid @RequestBody GradeUpdateRequest req) {
        return ResponseEntity.ok(enrollmentService.updateGrade(id, req.getGrade()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        enrollmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
