package com.joel.learn.course.domain.services;

import com.joel.learn.course.domain.models.User;

import java.util.UUID;

public interface UserService {

    void save(User user);

    void delete(UUID userId);


    User optionalUser(UUID userId);

}
