package com.university.sms.service;

import com.university.sms.dto.CourseDTO;

import java.util.List;

public interface CourseService {
    CourseDTO create(CourseDTO dto);
    CourseDTO getById(Long id);
    List<CourseDTO> getAll();
    CourseDTO update(Long id, CourseDTO dto);
    void delete(Long id);
}
