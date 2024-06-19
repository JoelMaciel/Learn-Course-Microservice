package com.joel.learn.course.domain.excptions;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

public class CourseNotFoundException extends EntityNotFoundException {

    public CourseNotFoundException(String message) {
        super(message);
    }

    public CourseNotFoundException(UUID courseId) {
        this(String.format("Course %s not found in database", courseId));
    }

}
