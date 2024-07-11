package com.joel.learn.course.domain.services.impl;

import com.joel.learn.course.domain.dtos.CourseRequestDTO;
import com.joel.learn.course.domain.dtos.PurchaseEventDTO;
import com.joel.learn.course.domain.models.Course;
import com.joel.learn.course.domain.repositories.CourseRepository;
import com.joel.learn.course.utils.AuthenticationHelper;
import com.joel.learn.course.utils.TestUtils;
import io.restassured.RestAssured;
import org.flywaydb.core.Flyway;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CourseControllerIntegrationIT {

    public static final String MSG_PURCHASE_SUCCESS = "Purchase Completed Successfully";
    public static final String THERE_IS_ALREADY_A_COURSE_WITH_THAT_TITLE = "Course cannot be saved because there is already a course with that title";

    @Autowired
    private Flyway flyway;

    @LocalServerPort
    private int port;

    private CourseRequestDTO courseRequestDTO;
    private UUID invalidCourseId;
    private String validCourseId;
    String tokenAdmin;
    String tokenStudent;


    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "/learn-course/api/courses";

        courseRequestDTO = TestUtils.getMockCourseRequestDTO();
        validCourseId = "0a657c35-e0ed-44e0-b25c-3afbeadd643e";
        invalidCourseId = UUID.randomUUID();
        tokenAdmin = AuthenticationHelper.getJwtTokenAdmin();
        tokenStudent = AuthenticationHelper.getJwtTokenForUserStudent();
        flyway.migrate();
    }

    @Test
    @DisplayName("Given Pageable, When FindAll Courses Then Should Return Page Of CourseDTO")
    void givenPageable_whenFindAllCourses_thenShouldReturnPageOfCourseDTO() {
        int page = 0;
        int size = 1;
        String sort = "title,asc";

        given()
                .auth().oauth2(tokenAdmin)
                .param("page", page)
                .param("size", size)
                .param("sort", sort)
             .when()
                .get()
             .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.size()", equalTo(size))
                .body("number", equalTo(page))
                .body("size", equalTo(size))
                .body("totalElements", Matchers.greaterThanOrEqualTo(size));
    }

    @Test
    @DisplayName("Given Valid CourseRequestDTO, When Save Course Then Should Save Course Successfully")
    void givenValidCourseRequestDTO_WhenSaveCourse_ThenShouldSaveCourseSuccessfully() {
        given()
                .contentType("application/json")
                .auth().oauth2(tokenAdmin)
                .body(courseRequestDTO)
             .when()
                .post()
             .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("title", equalTo("Course of Java"))
                .body("subtitle", equalTo("Learn Java from scratch"))
                .body("price", equalTo(90.0f));

    }

    @Test
    @DisplayName("Given Title Course Already Exists, When Save Course, Then Should Thrown TitleAlreadyExistsException And StatusCode 409")
    void givenTitleCourseAlreadyExists_WhenSaveCourse_ThenShouldThrownTitleAlreadyExistsExceptionAndStatusCode409() {
        CourseRequestDTO courseRequestDTO = TestUtils.getMockTitleAlreadyExistsDTO();
        given()
                .contentType("application/json")
                .auth().oauth2(tokenAdmin)
                .body(courseRequestDTO)
             .when()
                .post()
             .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("title", equalTo("Invalid data"))
                .body("userMessage", equalTo(THERE_IS_ALREADY_A_COURSE_WITH_THAT_TITLE));
    }

    @Test
    @DisplayName("Given Invalid Credentials, When Save Course Then Should Return Status Code 403")
    void givenInvalidCredentials_WhenSaveCourse_ThenShouldReturnStatusCode403() {
        given()
                .contentType("application/json")
                .auth().oauth2(tokenStudent)
                .body(courseRequestDTO)
             .when()
                .post()
             .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Given Invalid CourseRequestDTO, When Save Course Then Should Throw StatusCode 400")
    void givenInvalidCourseRequestDTO_whenSaveCourse_thenShouldThrowStatusCode400() {
        CourseRequestDTO invalidCourseRequestDTO = TestUtils.getMockInvalidCourseRequestDTO();

        given()
                .contentType("application/json")
                .auth().oauth2(tokenAdmin)
                .body(invalidCourseRequestDTO)
             .when()
                .post()
             .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("objects.find { it.name == 'title' }.userMessage", equalTo( "Title must be at least 6 characters."))
                .body("objects.find { it.name == 'subtitle' }.userMessage" ,equalTo("Subtitle must be at least 8 characters."))
                .body("objects.find { it.name == 'price' }.userMessage", equalTo("Price must have a value greater than zero."));
    }

    @Test
    @DisplayName("Given Valid CourseId, When FindById  Then Should Return CourseDTO And StatusCode 200")
    void givenValidCourseId_WhenFindById_ThenShouldReturnCourseDTOAndStatusCode200() {
        given()
                .auth().oauth2(tokenStudent)
                .contentType("application/json")
                .pathParam("courseId", validCourseId)
             .when()
                .get("/{courseId}")
             .then()
                .statusCode(HttpStatus.OK.value())
                .body("title", equalTo( "Java Developer"))
                .body("subtitle" ,equalTo("Java advanced  for developers"))
                .body("price", equalTo(99.99F));
    }

    @Test
    @DisplayName("Given Invalid CourseId, When FindById , Then Should Throw CourseNotFoundException StatusCode 404")
    void givenInvalidCourseId_WhenFindById_ThenShouldThrownCourseNotFoundExceptionAndStatusCode404() {
        given()
                .auth().oauth2(tokenStudent)
                .contentType("application/json")
                .pathParam("courseId", invalidCourseId)
             .when()
                .get("/{courseId}")
             .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo("Resource Not Found"))
                .body("detail", equalTo( "Course " + invalidCourseId +  " not found in database"));
    }

    @Test
    @DisplayName("Given Invalid Token, When FindById , Then Should Throw Unauthorized And StatusCode 401")
    void givenInvalidToken_WhenFindById_ThenShouldThrownUnauthorizedAndStatusCode401() {
        String invalidToken = TestUtils.getInvalidToken();

        given()
                .auth().oauth2(invalidToken)
                .contentType("application/json")
                .pathParam("courseId", validCourseId)
             .when()
                .get("/{courseId}")
             .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("error", equalTo("Unauthorized"));

    }

    @Test
    @DisplayName("Given Valid CourseRequestDTO And CourseId, When Update Course Then Should Return CourseDTO And StatusCode 200")
    void givenValidCourseRequestDTOAndCourseId_whenUpdateCourse_thenShouldReturnCourseDTOStatusCode200() {

        given()
                .auth().oauth2(tokenAdmin)
                .contentType("application/json")
                .pathParam("courseId", validCourseId)
                .body(courseRequestDTO)
             .when()
                .put("/{courseId}")
             .then()
                .statusCode(HttpStatus.OK.value())
                .body("title", equalTo( "Course of Java"))
                .body("subtitle" ,equalTo("Learn Java from scratch"))
                .body("price", equalTo(90.0F));
    }

    @Test
    @DisplayName("Given Invalid CourseRequestDTO, When Update Course Then Should Throw StatusCode 400")
    void givenInvalidCourseRequestDTO_whenUpdateCourse_thenShouldThrowStatusCode400() {
        CourseRequestDTO invalidCourseRequestDTO = TestUtils.getMockInvalidCourseRequestDTO();

        given()
                .auth().oauth2(tokenAdmin)
                .contentType("application/json")
                .pathParam("courseId", validCourseId)
                .body(invalidCourseRequestDTO)
             .when()
                .put("/{courseId}")
             .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("objects.find { it.name == 'title' }.userMessage", equalTo( "Title must be at least 6 characters."))
                .body("objects.find { it.name == 'subtitle' }.userMessage" ,equalTo("Subtitle must be at least 8 characters."))
                .body("objects.find { it.name == 'price' }.userMessage", equalTo("Price must have a value greater than zero."));
    }

    @Test
    @DisplayName("Given Invalid CourseId, When Update, Then Should Throw CourseNotFoundException And StatusCode 404")
    void givenInvalidCourseId_WhenUpdate_ThenShouldThrownCourseNotFoundExceptionAndStatusCode404() {

        given()
                .auth().oauth2(tokenAdmin)
                .body(courseRequestDTO)
                .contentType("application/json")
                .pathParam("courseId", invalidCourseId)
             .when()
                .put("/{courseId}")
             .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo("Resource Not Found"))
                .body("userMessage", equalTo("Course " + invalidCourseId + " not found in database"));
    }

    @Test
    @DisplayName("Given Valid CourseId, When Delete, Then Should Return StatusCode 204")
    void givenValidCourseId_WhenDelete_ThenShouldReturnStatusCode204() {

        given()
                .auth().oauth2(tokenAdmin)
                .contentType("application/json")
                .pathParam("courseId", validCourseId)
             .when()
                .delete("/{courseId}")
             .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("Given Invalid CourseId, When Delete, Then Should Thrown CourseNotFoundException StatusCode 404")
    void givenInvalidCourseId_WhenDelete_ThenShouldThrownCourseNotFoundExceptionStatusCode404() {

        given()
                .auth().oauth2(tokenAdmin)
                .contentType("application/json")
                .pathParam("courseId", invalidCourseId)
             .when()
                .delete("/{courseId}")
             .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title",equalTo("Resource Not Found"))
                .body("userMessage", equalTo("Course " + invalidCourseId + " not found in database"));
    }

    @Test
    @DisplayName("Given Valid PurchaseEventDTO, When Save Purchase Event , Then Should Return StatusCode 201")
    void givenValidPurchaseEventDTO_whenSavePurchaseEvent_thenShouldReturnStatusCode201() {
        PurchaseEventDTO purchaseEventDTO = TestUtils.getMockPurchaseEventDTO();

        given()
                .auth().oauth2(tokenStudent)
                .contentType("application/json")
                .body(purchaseEventDTO)
             .when()
                .post("/purchases")
             .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo(MSG_PURCHASE_SUCCESS));
    }
}
