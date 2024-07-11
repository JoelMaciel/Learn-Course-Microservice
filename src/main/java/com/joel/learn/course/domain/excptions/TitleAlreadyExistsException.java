package com.joel.learn.course.domain.excptions;

import org.springframework.dao.DataIntegrityViolationException;

public class TitleAlreadyExistsException extends DataIntegrityViolationException {

    public TitleAlreadyExistsException(String message) {
        super(message);
    }
}
