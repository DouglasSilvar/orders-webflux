package com.order.challenge.exception.handler;

import com.order.challenge.exception.ErrorResponse;
import com.order.challenge.exception.OrderNotFoundException;
import com.order.challenge.exception.OrderPersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleOrderNotFoundException_ShouldReturnNotFoundStatusAndCorrectBody() {
        String errorMessage = "Order with ID 123 not found.";
        OrderNotFoundException exception = new OrderNotFoundException(errorMessage);

        Mono<ResponseEntity<ErrorResponse>> responseMono = exceptionHandler.handleOrderNotFoundException(exception);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                    ErrorResponse errorResponse = response.getBody();
                    assertNotNull(errorResponse);
                    assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
                    assertEquals(errorMessage, errorResponse.getMessage());
                    assertNotNull(errorResponse.getTimestamp());
                })
                .verifyComplete();
    }

    @Test
    void handleOrderPersistenceException_ShouldReturnInternalServerErrorStatusAndCorrectBody() {
        String originalMessage = "Failed to insert into database.";
        OrderPersistenceException exception = new OrderPersistenceException(originalMessage);

        Mono<ResponseEntity<ErrorResponse>> responseMono = exceptionHandler.handleOrderPersistenceException(exception);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                    ErrorResponse errorResponse = response.getBody();
                    assertNotNull(errorResponse);
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
                    assertEquals("Database Persistence Error: " + originalMessage, errorResponse.getMessage());
                    assertNotNull(errorResponse.getTimestamp());
                })
                .verifyComplete();
    }
}