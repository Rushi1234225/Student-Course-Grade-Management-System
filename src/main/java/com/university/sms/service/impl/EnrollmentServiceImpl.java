package com.university.sms.service.impl;

import com.university.sms.dto.EnrollmentDTO;
import com.university.sms.exception.DuplicateResourceException;
import com.university.sms.exception.InvalidRequestException;
import com.university.sms.exception.ResourceNotFoundException;
import com.university.sms.model.Course;
import com.university.sms.model.Enrollment;
import com.university.sms.model.Grade;
import com.university.sms.model.Student;
import com.university.sms.repository.CourseRepository;
import com.university.sms.repository.EnrollmentRepository;
import com.university.sms.repository.StudentRepository;
import com.university.sms.service.EnrollmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
                                  StudentRepository studentRepository,
                                  CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public EnrollmentDTO create(EnrollmentDTO dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Student", dto.getStudentId()));
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Course", dto.getCourseId()));

        if (enrollmentRepository.existsByStudentIdAndCourseIdAndSemester(
                dto.getStudentId(), dto.getCourseId(), dto.getSemester())) {
            throw new DuplicateResourceException(
                    "This student is already enrolled in this course for semester " + dto.getSemester() + ".");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setSemester(dto.getSemester());
        enrollment.setGrade(parseGrade(dto.getGrade()));

        Enrollment saved = enrollmentRepository.save(enrollment);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentDTO getById(Long id) {
        return toDto(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getAll() {
        return enrollmentRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getByStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw ResourceNotFoundException.forEntity("Student", studentId);
        }
        return enrollmentRepository.findByStudentId(studentId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw ResourceNotFoundException.forEntity("Course", courseId);
        }
        return enrollmentRepository.findByCourseId(courseId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public EnrollmentDTO updateGrade(Long id, String grade) {
        Enrollment enrollment = findEntity(id);
        enrollment.setGrade(parseGrade(grade));
        return toDto(enrollmentRepository.save(enrollment));
    }

    @Override
    public void delete(Long id) {
        if (!enrollmentRepository.existsById(id)) {
            throw ResourceNotFoundException.forEntity("Enrollment", id);
        }
        enrollmentRepository.deleteById(id);
    }

    private Enrollment findEntity(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Enrollment", id));
    }

    private Grade parseGrade(String raw) {
        if (raw == null || raw.isBlank()) {
            return Grade.NOT_GRADED;
        }
        try {
            return Grade.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidRequestException(
                    "'" + raw + "' is not a valid grade. Valid values: A_PLUS, A, A_MINUS, B_PLUS, B, B_MINUS, " +
                            "C_PLUS, C, C_MINUS, D, F, NOT_GRADED.");
        }
    }

    private EnrollmentDTO toDto(Enrollment e) {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(e.getId());
        dto.setStudentId(e.getStudent().getId());
        dto.setCourseId(e.getCourse().getId());
        dto.setSemester(e.getSemester());
        dto.setGrade(e.getGrade().name());
        dto.setEnrolledOn(e.getEnrolledOn());
        dto.setStudentName(e.getStudent().getFirstName() + " " + e.getStudent().getLastName());
        dto.setCourseTitle(e.getCourse().getTitle());
        return dto;
    }
}
