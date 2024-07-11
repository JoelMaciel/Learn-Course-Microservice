package com.joel.learn.course.utils;

import static io.restassured.RestAssured.given;

public class AuthenticationHelper {

    public static final String URL_AUTHUSER_MICROSERVICE = "http://localhost:8080/learn-auth/api/auth/login";

    public static String getJwtTokenAdmin() {
        return given()
                .contentType("application/json")
                .body(TestUtils.getLoginAdminRequestDTO())
             .when()
                .post(URL_AUTHUSER_MICROSERVICE)
             .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    public static String getJwtTokenForUserStudent() {
        return given()
                .contentType("application/json")
                .body(TestUtils.getLoginStudentRequestDTO())
             .when()
                .post(URL_AUTHUSER_MICROSERVICE)
             .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}

