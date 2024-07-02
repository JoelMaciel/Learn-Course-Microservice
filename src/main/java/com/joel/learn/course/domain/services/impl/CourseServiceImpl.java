package com.joel.learn.course.domain.services.impl;

import com.joel.learn.course.api.publishers.PurchaseCommandPublisher;
import com.joel.learn.course.domain.dtos.CourseDTO;
import com.joel.learn.course.domain.dtos.CourseRequestDTO;
import com.joel.learn.course.domain.dtos.PurchaseEventDTO;
import com.joel.learn.course.domain.excptions.CourseNotFoundException;
import com.joel.learn.course.domain.excptions.TitleAlreadyExistsException;
import com.joel.learn.course.domain.models.Course;
import com.joel.learn.course.domain.repositories.CourseRepository;
import com.joel.learn.course.domain.services.CourseService;
import com.joel.learn.course.domain.services.UserService;
import com.joel.learn.course.domain.services.converter.CourseConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
@Service
public class CourseServiceImpl implements CourseService {

    public static final String THERE_IS_ALREADY_A_BOOK_WITH_THIS_TITLE = "Book cannot be saved because there is already a book with that title";
    private final CourseRepository courseRepository;
    private final CourseConverter courseConverter;
    private final PurchaseCommandPublisher purchaseCommandPublisher;
    private final UserService userService;

    @Override
    public Page<CourseDTO> findAll(Pageable pageable) {
        Page<Course> courses = courseRepository.findAll(pageable);
        return courseConverter.toPageDTO(courses);
    }

    @Override
    public CourseDTO findById(UUID courseId) {
        Course course = optionalCourse(courseId);
        return courseConverter.toDTO(course);
    }

    @Transactional
    @Override
    public CourseDTO update(UUID courseId, CourseRequestDTO courseRequestDTO) {
        Course course = optionalCourse(courseId);
        Course courseUpdated = courseConverter.toUpdatedEntity(course, courseRequestDTO);
        return courseConverter.toDTO(courseRepository.save(courseUpdated));
    }

    @Transactional
    @Override
    public CourseDTO save(CourseRequestDTO courseRequestDTO) {
        Course course = courseConverter.toEntity(courseRequestDTO);
        validateTitle(courseRequestDTO);

        log.info("Saving course from CourseId : {}", course.getCourseId());
        return courseConverter.toDTO(courseRepository.save(course));
    }

    @Override
    public void purchaseEvent(PurchaseEventDTO purchaseEventDTO) {
        userService.optionalUser(purchaseEventDTO.getUserId());

        Course course = optionalCourse(purchaseEventDTO.getCourseId());
        purchaseEventDTO.setTitle(course.getTitle());

        purchaseCommandPublisher.publishPurchaseEvent(purchaseEventDTO);
    }

    @Transactional
    @Override
    public void delete(UUID courseId) {
        optionalCourse(courseId);
        courseRepository.deleteById(courseId);
        log.info("CourseId deleted: {}", courseId);
    }

    @Override
    public Course optionalCourse(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
    }

    private void validateTitle(CourseRequestDTO courseRequestDTO) {
        if (courseRepository.existsByTitle(courseRequestDTO.getTitle())) {
            throw new TitleAlreadyExistsException(THERE_IS_ALREADY_A_BOOK_WITH_THIS_TITLE);
        }
    }
}
