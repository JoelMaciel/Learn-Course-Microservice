package com.joel.learn.course.domain.services.impl.utils;


import com.joel.learn.course.domain.dtos.CourseDTO;
import com.joel.learn.course.domain.dtos.CourseRequestDTO;
import com.joel.learn.course.domain.dtos.PurchaseEventDTO;
import com.joel.learn.course.domain.models.Course;

import java.time.OffsetDateTime;
import java.util.UUID;

public class TestUtils {

    public static CourseRequestDTO getMockCourseRequestDTO() {
        return CourseRequestDTO.builder()
                .title("Course of Java")
                .subtitle("Learn Java from scratch")
                .price(90.0)
                .build();
    }

    public static CourseDTO getMockCourseDTO() {
        return CourseDTO.builder()
                .courseId(UUID.fromString("081caae9-358d-4ded-9a37-e2a66573549a"))
                .title("Course of Java")
                .subtitle("Learn Java from scratch")
                .price(90.0)
                .creationDate(OffsetDateTime.now())
                .build();
    }

    public static Course getMockCourseModel() {
        return Course.builder()
                .courseId(UUID.randomUUID())
                .title("Introduction to Java")
                .creationDate(OffsetDateTime.now())
                .build();
    }

    public static Course getUpdatedCourseModel(Course course, CourseRequestDTO courseRequestDTO) {
        return course.toBuilder()
                .title(courseRequestDTO.getTitle())
                .subtitle(courseRequestDTO.getSubtitle())
                .price(courseRequestDTO.getPrice())
                .build();
    }

    public static PurchaseEventDTO getMockPurchaseEventDTO() {
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        return PurchaseEventDTO.builder()
                .userId(userId)
                .courseId(UUID.fromString("081caae9-358d-4ded-9a37-e2a66573549a"))
                .title("Course of Java")
                .price(90.0)
                .orderDate(OffsetDateTime.now())
                .build();
    }

}
