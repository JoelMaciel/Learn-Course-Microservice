package com.joel.learn.course.domain.services.converter;

import com.joel.learn.course.domain.dtos.CourseDTO;
import com.joel.learn.course.domain.dtos.CourseRequestDTO;
import com.joel.learn.course.domain.models.Course;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CourseConverter {

    public Page<CourseDTO> toPageDTO(Page<Course> courses) {
        return courses.map(this::toDTO);
    }

    public CourseDTO toDTO(Course course) {
        return CourseDTO.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .subtitle(course.getSubtitle())
                .price(course.getPrice())
                .creationDate(course.getCreationDate())
                .build();
    }

    public Course toEntity(CourseRequestDTO courseRequestDTO) {
        return Course.builder()
                .title(courseRequestDTO.getTitle())
                .subtitle(courseRequestDTO.getSubtitle())
                .price(courseRequestDTO.getPrice())
                .creationDate(LocalDateTime.now())
                .build();
    }

    public Course toUpdatedEntity(Course course, CourseRequestDTO courseRequestDTO) {
        return course.toBuilder()
                .title(courseRequestDTO.getTitle())
                .subtitle(courseRequestDTO.getSubtitle())
                .price(courseRequestDTO.getPrice())
                .build();
    }
}
