package com.university.sms.service.impl;

import com.university.sms.dto.CourseDTO;
import com.university.sms.exception.DuplicateResourceException;
import com.university.sms.exception.ResourceNotFoundException;
import com.university.sms.model.Course;
import com.university.sms.repository.CourseRepository;
import com.university.sms.service.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public CourseDTO create(CourseDTO dto) {
        if (courseRepository.existsByCourseCode(dto.getCourseCode())) {
            throw new DuplicateResourceException(
                    "A course with code '" + dto.getCourseCode() + "' already exists.");
        }
        Course course = toEntity(dto);
        Course saved = courseRepository.save(course);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDTO getById(Long id) {
        return toDto(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getAll() {
        return courseRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public CourseDTO update(Long id, CourseDTO dto) {
        Course existing = findEntity(id);
        if (!existing.getCourseCode().equals(dto.getCourseCode())
                && courseRepository.existsByCourseCode(dto.getCourseCode())) {
            throw new DuplicateResourceException(
                    "A course with code '" + dto.getCourseCode() + "' already exists.");
        }
        existing.setCourseCode(dto.getCourseCode());
        existing.setTitle(dto.getTitle());
        existing.setCreditHours(dto.getCreditHours());
        existing.setDepartment(dto.getDepartment());
        existing.setDescription(dto.getDescription());
        return toDto(courseRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        if (!courseRepository.existsById(id)) {
            throw ResourceNotFoundException.forEntity("Course", id);
        }
        courseRepository.deleteById(id);
    }

    private Course findEntity(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Course", id));
    }

    private Course toEntity(CourseDTO dto) {
        Course c = new Course();
        c.setCourseCode(dto.getCourseCode());
        c.setTitle(dto.getTitle());
        c.setCreditHours(dto.getCreditHours());
        c.setDepartment(dto.getDepartment());
        c.setDescription(dto.getDescription());
        return c;
    }

    private CourseDTO toDto(Course c) {
        return new CourseDTO(
                c.getId(),
                c.getCourseCode(),
                c.getTitle(),
                c.getCreditHours(),
                c.getDepartment(),
                c.getDescription(),
                c.getEnrollments() != null ? c.getEnrollments().size() : 0
        );
    }
}
