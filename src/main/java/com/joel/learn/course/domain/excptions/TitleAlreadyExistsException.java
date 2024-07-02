package com.joel.learn.course.domain.excptions;

public class TitleAlreadyExistsException extends BusinessException{

    public TitleAlreadyExistsException(String message) {
        super(message);
    }
}
