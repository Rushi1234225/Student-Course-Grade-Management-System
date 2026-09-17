package com.university.sms.service;

import com.university.sms.dto.StudentDTO;

import java.util.List;

public interface StudentService {
    StudentDTO create(StudentDTO dto);
    StudentDTO getById(Long id);
    List<StudentDTO> getAll();
    StudentDTO update(Long id, StudentDTO dto);
    void delete(Long id);
    double calculateGpa(Long studentId);
}
