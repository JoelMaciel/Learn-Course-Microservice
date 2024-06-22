package com.joel.learn.course.domain.excptions;

public class PasswordMismatchedException extends BusinessException {

    public PasswordMismatchedException(String message) {
        super(message);
    }
}
