package com.joel.learn.course.domain.services;

import com.joel.learn.course.domain.dtos.CourseDTO;
import com.joel.learn.course.domain.dtos.CourseRequestDTO;
import com.joel.learn.course.domain.models.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CourseService {

    CourseDTO save (CourseRequestDTO courseRequestDTO);

    void delete(UUID courseId);

    Course optionalCourse(UUID courseId);

    Page<CourseDTO> findAll(Pageable pageable);

    CourseDTO findById(UUID courseId);

    CourseDTO update(UUID courseId, CourseRequestDTO courseRequestDTO);
}
