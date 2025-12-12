package com.order.challenge.exception.handler;

import com.order.challenge.exception.ErrorResponse;
import com.order.challenge.exception.OrderNotFoundException;
import com.order.challenge.exception.OrderPersistenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleOrderNotFoundException(OrderNotFoundException ex) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );

        return Mono.just(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(error)
        );
    }

    @ExceptionHandler(OrderPersistenceException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleOrderPersistenceException(OrderPersistenceException ex) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), // 500
                "Database Persistence Error: " + ex.getMessage(),
                LocalDateTime.now()
        );

        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error));
    }
}
