package com.university.sms.controller;

import com.university.sms.dto.StudentDTO;
import com.university.sms.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<StudentDTO> create(@Valid @RequestBody StudentDTO dto) {
        StudentDTO created = studentService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAll() {
        return ResponseEntity.ok(studentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> update(@PathVariable Long id, @Valid @RequestBody StudentDTO dto) {
        return ResponseEntity.ok(studentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/gpa")
    public ResponseEntity<Map<String, Object>> gpa(@PathVariable Long id) {
        double gpa = studentService.calculateGpa(id);
        return ResponseEntity.ok(Map.of("studentId", id, "gpa", gpa));
    }
}
