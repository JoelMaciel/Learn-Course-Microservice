package com.joel.learn.course.domain.services.impl;

import com.joel.learn.course.api.publishers.PurchaseCommandPublisher;
import com.joel.learn.course.domain.dtos.CourseDTO;
import com.joel.learn.course.domain.dtos.CourseRequestDTO;
import com.joel.learn.course.domain.dtos.PurchaseEventDTO;
import com.joel.learn.course.domain.excptions.CourseNotFoundException;
import com.joel.learn.course.domain.excptions.TitleAlreadyExistsException;
import com.joel.learn.course.domain.models.Course;
import com.joel.learn.course.domain.repositories.CourseRepository;
import com.joel.learn.course.domain.services.UserService;
import com.joel.learn.course.domain.services.converter.CourseConverter;
import com.joel.learn.course.domain.services.impl.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceImplTest {

    public static final String THERE_IS_ALREADY_A_COURSE_WITH_THIS_TITLE = "Course cannot be saved because there is already a course with that title";
    public static final String COURSE_NOT_FOUND = "Course e53b4d24-6b49-4b7e-9f0b-69f77d4d64b8 not found in database";

    @InjectMocks
    private CourseServiceImpl courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseConverter courseConverter;

    @Mock
    private PurchaseCommandPublisher purchaseCommandPublisher;

    @Mock
    private UserService userService;

    private Course course;
    private CourseDTO courseDTO;
    private CourseRequestDTO courseRequestDTO;
    private UUID courseId;
    private UUID invalidCourseId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        course = TestUtils.getMockCourseModel();
        courseDTO = TestUtils.getMockCourseDTO();
        courseRequestDTO = TestUtils.getMockCourseRequestDTO();
        courseId = UUID.fromString("081caae9-358d-4ded-9a37-e2a66573549a");
        invalidCourseId = UUID.fromString("e53b4d24-6b49-4b7e-9f0b-69f77d4d64b8");

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseRepository.findById(invalidCourseId)).thenReturn(Optional.empty());
        when(courseConverter.toDTO(course)).thenReturn(courseDTO);
        when(courseConverter.toEntity(courseRequestDTO)).thenReturn(course);
        when(courseConverter.toUpdatedEntity(course, courseRequestDTO)).thenReturn(course);
    }

    @Test
    @DisplayName("Given Pageable When FindAll Then Return Page of CourseDTOs")
    void givenPageable_whenFindAll_thenReturnsPageOfCourseDTOs() {
        Pageable pageable = Pageable.ofSize(10).withPage(0);

        List<Course> mockCourses = Collections.singletonList(course);
        Page<Course> mockCoursePage = new PageImpl<>(mockCourses, pageable, mockCourses.size());
        when(courseRepository.findAll(pageable)).thenReturn(mockCoursePage);

        List<CourseDTO> mockCourseDTOs = Collections.singletonList(courseDTO);
        Page<CourseDTO> mockCourseDTOPage = new PageImpl<>(mockCourseDTOs, pageable, mockCourseDTOs.size());
        when(courseConverter.toPageDTO(mockCoursePage)).thenReturn(mockCourseDTOPage);

        Page<CourseDTO> result = courseService.findAll(pageable);

        assertNotNull(result);
        assertEquals(mockCourseDTOPage.getTotalElements(), result.getTotalElements());
        assertEquals(mockCourseDTOPage.getContent().size(), result.getContent().size());
        assertEquals(mockCourseDTOPage.getContent().get(0).getCourseId(), result.getContent().get(0).getCourseId());
        assertEquals(mockCourseDTOPage.getContent().get(0).getTitle(), result.getContent().get(0).getTitle());
        verify(courseRepository, times(1)).findAll(pageable);
        verify(courseConverter, times(1)).toPageDTO(mockCoursePage);
    }

    @Test
    @DisplayName("Given Valid CourseId When FindById Then Return CourseDTO")
    void givenValidCourseId_whenFindById_thenReturnsCourseDTO() {
        CourseDTO result = courseService.findById(courseId);

        assertNotNull(result);
        assertEquals(courseDTO.getCourseId(), result.getCourseId());
        assertEquals(courseDTO.getTitle(), result.getTitle());
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseConverter, times(1)).toDTO(course);
    }

    @Test
    @DisplayName("Given Invalid CourseId When FindById Then Throw CourseNotFoundException")
    void givenInvalidCourseId_whenFindById_thenThrowCourseNotFoundException() {
        CourseNotFoundException exception = assertThrows(CourseNotFoundException.class, () -> {
            courseService.findById(invalidCourseId);
        });

        assertEquals(COURSE_NOT_FOUND, exception.getMessage());
        verify(courseRepository, times(1)).findById(invalidCourseId);
        verify(courseConverter, never()).toDTO(any(Course.class));
    }

    @Test
    @DisplayName("Given Valid CourseId and CourseRequestDTO When Update Then Return Updated CourseDTO")
    void givenValidCourseIdAndCourseRequestDTO_WhenUpdate_ThenReturnsUpdatedCourseDTO() {
        Course courseUpdated = TestUtils.getUpdatedCourseModel(course, courseRequestDTO);

        when(courseConverter.toUpdatedEntity(course, courseRequestDTO)).thenReturn(courseUpdated);
        when(courseRepository.save(any(Course.class))).thenReturn(courseUpdated);
        when(courseConverter.toDTO(any(Course.class))).thenReturn(courseDTO);

        CourseDTO result = courseService.update(courseId, courseRequestDTO);

        assertNotNull(result);
        assertEquals(courseDTO.getCourseId(), result.getCourseId());
        assertEquals(courseRequestDTO.getTitle(), result.getTitle());
        assertEquals(courseRequestDTO.getSubtitle(), result.getSubtitle());
        assertEquals(courseRequestDTO.getPrice(), result.getPrice());

        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, times(1)).save(any(Course.class));
        verify(courseConverter, times(1)).toUpdatedEntity(course, courseRequestDTO);
        verify(courseConverter, times(1)).toDTO(any(Course.class));
    }

    @Test
    @DisplayName("Given Invalid CourseId When Update Then Throw CourseNotFoundException")
    void givenInvalidCourseId_whenUpdate_thenThrowCourseNotFoundException() {
        CourseNotFoundException exception = assertThrows(CourseNotFoundException.class, () -> {
            courseService.update(invalidCourseId, courseRequestDTO);
        });

        assertEquals(COURSE_NOT_FOUND, exception.getMessage());
        verify(courseRepository, times(1)).findById(invalidCourseId);
        verify(courseRepository, never()).save(any(Course.class));
        verify(courseConverter, never()).toUpdatedEntity(any(Course.class), any(CourseRequestDTO.class));
        verify(courseConverter, never()).toDTO(any(Course.class));
    }

    @Test
    @DisplayName("Given CourseRequestDTO When Save Then Return CourseDTO")
    void givenCourseRequestDTO_whenSave_thenReturnsCourseDTO() {
        when(courseRepository.existsByTitle(courseRequestDTO.getTitle())).thenReturn(false);

        Course courseSaved = TestUtils.getMockCourseModel();
        when(courseRepository.save(any(Course.class))).thenReturn(courseSaved);
        when(courseConverter.toDTO(any(Course.class))).thenReturn(courseDTO);

        CourseDTO result = courseService.save(courseRequestDTO);

        assertNotNull(result);
        assertEquals(courseDTO.getCourseId(), result.getCourseId());
        assertEquals(courseRequestDTO.getTitle(), result.getTitle());
        assertEquals(courseRequestDTO.getSubtitle(), result.getSubtitle());
        assertEquals(courseRequestDTO.getPrice(), result.getPrice());

        verify(courseRepository, times(1)).existsByTitle(courseRequestDTO.getTitle());
        verify(courseRepository, times(1)).save(any(Course.class));
        verify(courseConverter, times(1)).toEntity(courseRequestDTO);
        verify(courseConverter, times(1)).toDTO(any(Course.class));
    }

    @Test
    @DisplayName("Given CourseRequestDTO With Existing Title When Save Then Throw TitleAlreadyExistsException")
    void givenCourseRequestDTOWithExistingTitle_whenSave_thenThrowTitleAlreadyExistsException() {
        when(courseRepository.existsByTitle(courseRequestDTO.getTitle())).thenReturn(true);

        TitleAlreadyExistsException exception = assertThrows(TitleAlreadyExistsException.class, () -> {
            courseService.save(courseRequestDTO);
        });

        assertEquals(THERE_IS_ALREADY_A_COURSE_WITH_THIS_TITLE, exception.getMessage());
        verify(courseRepository, times(1)).existsByTitle(courseRequestDTO.getTitle());
        verify(courseRepository, never()).save(any(Course.class));
        verify(courseConverter, never()).toDTO(any(Course.class));
    }

    @Test
    @DisplayName("Given Valid CourseId When Delete Then Should Delete Course")
    void givenValidCourseId_whenDelete_thenShouldDeleteCourse() {
        courseService.delete(courseId);

        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, times(1)).deleteById(courseId);
    }

    @Test
    @DisplayName("Given Invalid CourseId When Delete Then Throw CourseNotFoundException")
    void givenInvalidCourseId_whenDelete_thenThrowCourseNotFoundException() {
        CourseNotFoundException exception = assertThrows(CourseNotFoundException.class, () -> {
            courseService.delete(invalidCourseId);
        });

        assertEquals(COURSE_NOT_FOUND, exception.getMessage());
        verify(courseRepository, times(1)).findById(invalidCourseId);
        verify(courseRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Given PurchaseEventDTO When PurchaseEvent Then Should Publish PurchaseEvent")
    void givenPurchaseEventDTO_whenPurchaseEvent_thenShouldPublishPurchaseEvent() {
        PurchaseEventDTO purchaseEventDTO = TestUtils.getMockPurchaseEventDTO();

        courseService.purchaseEvent(purchaseEventDTO);

        verify(userService, times(1)).optionalUser(purchaseEventDTO.getUserId());
        verify(courseRepository, times(1)).findById(purchaseEventDTO.getCourseId());
        verify(purchaseCommandPublisher, times(1)).publishPurchaseEvent(purchaseEventDTO);
    }
}
