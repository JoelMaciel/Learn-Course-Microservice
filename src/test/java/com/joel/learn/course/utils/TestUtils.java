package com.joel.learn.course.utils;


import com.joel.learn.course.domain.dtos.CourseDTO;
import com.joel.learn.course.domain.dtos.CourseRequestDTO;
import com.joel.learn.course.domain.dtos.PurchaseEventDTO;
import com.joel.learn.course.domain.models.Course;
import com.joel.learn.course.dtos.LoginRequestDTO;

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

    public static CourseRequestDTO getMockTitleAlreadyExistsDTO() {
        return CourseRequestDTO.builder()
                .title("Java Developer")
                .subtitle("Learn Java from scratch")
                .price(33.0)
                .build();
    }

    public static CourseRequestDTO getMockInvalidCourseRequestDTO() {
        return CourseRequestDTO.builder()
                .title("")
                .subtitle("")
                .price(-15.0)
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
        UUID userId = UUID.fromString("e53b4d24-6b49-4b7e-9f0b-69f77d4d64b8");

        return PurchaseEventDTO.builder()
                .userId(userId)
                .courseId(UUID.fromString("0a657c35-e0ed-44e0-b25c-3afbeadd643e"))
                .title("Course of Java")
                .price(90.0)
                .orderDate(OffsetDateTime.now())
                .build();
    }

    public static LoginRequestDTO getLoginAdminRequestDTO() {
        return LoginRequestDTO.builder()
                .username("admintest")
                .password("12345678")
                .build();
    }

    public static LoginRequestDTO getLoginStudentRequestDTO() {
        return LoginRequestDTO.builder()
                .username("vianatest")
                .password("12345678")
                .build();
    }

    public static String getInvalidToken() {
        return "invalidTokenMiJ9.invalidTokenhMy01MzIwLTQ4YJinvalidTokenkY2MzZmQiLCJyb2x" +
                "lcyI6IlJPTEVfU1invalidTokenRVREVOVCIsImlhdCwiZXinvalidTokenhwIjoxNzIwNjMwMTUwfQ.l3UNuz7Pb14yEgHTR9g" +
                "p-wAxLdiuXZqIrR9aCJp8HnRquM4gJne_nxxGWOCoP8xjUJPcs8qeR86cPgsKlsjwkg";
    }
}
