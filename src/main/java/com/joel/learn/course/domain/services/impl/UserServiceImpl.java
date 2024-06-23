package com.joel.learn.course.domain.services.impl;

import com.joel.learn.course.domain.excptions.UserNotFoundException;
import com.joel.learn.course.domain.models.User;
import com.joel.learn.course.domain.repositories.UserRepository;
import com.joel.learn.course.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public void save(User user) {
        userRepository.save(user);
        log.info("UserId saved : {}", user.getUserId());
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        optionalUser(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public User optionalUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
