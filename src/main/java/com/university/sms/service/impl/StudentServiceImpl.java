package com.university.sms.service.impl;

import com.university.sms.dto.StudentDTO;
import com.university.sms.exception.DuplicateResourceException;
import com.university.sms.exception.ResourceNotFoundException;
import com.university.sms.model.Enrollment;
import com.university.sms.model.Grade;
import com.university.sms.model.Student;
import com.university.sms.repository.EnrollmentRepository;
import com.university.sms.repository.StudentRepository;
import com.university.sms.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public StudentServiceImpl(StudentRepository studentRepository, EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public StudentDTO create(StudentDTO dto) {
        if (studentRepository.existsByRegistrationNumber(dto.getRegistrationNumber())) {
            throw new DuplicateResourceException(
                    "A student with registration number '" + dto.getRegistrationNumber() + "' already exists.");
        }
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException(
                    "A student with email '" + dto.getEmail() + "' already exists.");
        }
        Student student = toEntity(dto);
        Student saved = studentRepository.save(student);
        return toDto(saved, 0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO getById(Long id) {
        Student student = findEntity(id);
        return toDto(student, calculateGpa(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> getAll() {
        return studentRepository.findAll().stream()
                .map(s -> toDto(s, calculateGpa(s.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public StudentDTO update(Long id, StudentDTO dto) {
        Student existing = findEntity(id);

        // If the registration number or email is being changed, ensure the
        // new value isn't already taken by a *different* student.
        if (!existing.getRegistrationNumber().equals(dto.getRegistrationNumber())
                && studentRepository.existsByRegistrationNumber(dto.getRegistrationNumber())) {
            throw new DuplicateResourceException(
                    "A student with registration number '" + dto.getRegistrationNumber() + "' already exists.");
        }
        if (!existing.getEmail().equals(dto.getEmail())
                && studentRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException(
                    "A student with email '" + dto.getEmail() + "' already exists.");
        }

        existing.setRegistrationNumber(dto.getRegistrationNumber());
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setDateOfBirth(dto.getDateOfBirth());
        existing.setMajor(dto.getMajor());
        if (dto.getEnrollmentDate() != null) {
            existing.setEnrollmentDate(dto.getEnrollmentDate());
        }

        Student saved = studentRepository.save(existing);
        return toDto(saved, calculateGpa(id));
    }

    @Override
    public void delete(Long id) {
        if (!studentRepository.existsById(id)) {
            throw ResourceNotFoundException.forEntity("Student", id);
        }
        studentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public double calculateGpa(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw ResourceNotFoundException.forEntity("Student", studentId);
        }
        List<Enrollment> graded = enrollmentRepository.findGradedEnrollmentsByStudent(studentId);
        if (graded.isEmpty()) {
            return 0.0;
        }
        double totalPoints = 0.0;
        int totalCredits = 0;
        for (Enrollment e : graded) {
            Grade g = e.getGrade();
            int credits = e.getCourse().getCreditHours();
            totalPoints += g.getGpaPoints() * credits;
            totalCredits += credits;
        }
        if (totalCredits == 0) {
            return 0.0;
        }
        double gpa = totalPoints / totalCredits;
        return Math.round(gpa * 100.0) / 100.0; // round to 2 decimal places
    }

    private Student findEntity(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Student", id));
    }

    private Student toEntity(StudentDTO dto) {
        Student s = new Student();
        s.setRegistrationNumber(dto.getRegistrationNumber());
        s.setFirstName(dto.getFirstName());
        s.setLastName(dto.getLastName());
        s.setEmail(dto.getEmail());
        s.setDateOfBirth(dto.getDateOfBirth());
        s.setMajor(dto.getMajor());
        if (dto.getEnrollmentDate() != null) {
            s.setEnrollmentDate(dto.getEnrollmentDate());
        }
        return s;
    }

    private StudentDTO toDto(Student s, double gpa) {
        return new StudentDTO(
                s.getId(),
                s.getRegistrationNumber(),
                s.getFirstName(),
                s.getLastName(),
                s.getEmail(),
                s.getDateOfBirth(),
                s.getMajor(),
                s.getEnrollmentDate(),
                gpa
        );
    }
}
