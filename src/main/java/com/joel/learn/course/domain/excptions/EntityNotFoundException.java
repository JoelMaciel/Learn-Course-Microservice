package com.joel.learn.course.domain.excptions;

public abstract class EntityNotFoundException extends BusinessException {

    protected EntityNotFoundException(String message) {
        super(message);
    }
}
