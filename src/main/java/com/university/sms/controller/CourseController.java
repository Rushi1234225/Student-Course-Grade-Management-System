package com.university.sms.controller;

import com.university.sms.dto.CourseDTO;
import com.university.sms.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<CourseDTO> create(@Valid @RequestBody CourseDTO dto) {
        CourseDTO created = courseService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAll() {
        return ResponseEntity.ok(courseService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> update(@PathVariable Long id, @Valid @RequestBody CourseDTO dto) {
        return ResponseEntity.ok(courseService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
