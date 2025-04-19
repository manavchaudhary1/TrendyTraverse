package com.manav.cartservice.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleUnauthorizedAccess() {
        // Arrange
        String errorMessage = "Access denied";
        UnauthorizedAccessException exception = new UnauthorizedAccessException(errorMessage);

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleUnauthorizedAccess(exception);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Map<String, String> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Forbidden", responseBody.get("error"));
        assertEquals(errorMessage, responseBody.get("message"));
    }

    @Test
    void handleCartNotFound() {
        // Arrange
        String errorMessage = "Cart not found";
        CartNotFoundException exception = new CartNotFoundException(errorMessage);

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleCartNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Not Found", responseBody.get("error"));
        assertEquals(errorMessage, responseBody.get("message"));
    }

    @Test
    void handleCustomException() {
        // Arrange
        String errorMessage = "Custom error occurred";
        CustomException exception = new CustomException(errorMessage);

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleCustomException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Bad Request", responseBody.get("error"));
        assertEquals(errorMessage, responseBody.get("message"));
    }

    @Test
    void handleGeneralException() {
        // Arrange
        String errorMessage = "Unexpected error";
        Exception exception = new Exception(errorMessage);

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleGeneralException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, String> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Internal Server Error", responseBody.get("error"));
        assertEquals("Something went wrong.", responseBody.get("message"));
    }
}