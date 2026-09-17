package com.university.sms.service;

import com.university.sms.dto.EnrollmentDTO;

import java.util.List;

public interface EnrollmentService {
    EnrollmentDTO create(EnrollmentDTO dto);
    EnrollmentDTO getById(Long id);
    List<EnrollmentDTO> getAll();
    List<EnrollmentDTO> getByStudent(Long studentId);
    List<EnrollmentDTO> getByCourse(Long courseId);
    EnrollmentDTO updateGrade(Long id, String grade);
    void delete(Long id);
}
