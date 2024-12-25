package org.example.hhplus.registrationsys.aop;

import java.time.LocalDateTime;
import org.example.hhplus.registrationsys.exception.DefaultErrorResponse;
import org.example.hhplus.registrationsys.exception.DefaultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiControllerAdvice.class);

    @ExceptionHandler(value = DefaultException.class)
    public ResponseEntity<DefaultErrorResponse> handleException(DefaultException e) {

        DefaultErrorResponse defaultErrorResponse = DefaultErrorResponse
            .builder()
            .status(e.getHttpStatus().value())
            .error(e.getHttpStatus().getReasonPhrase())
            .message(e.getMessage())
            .timestamp(LocalDateTime.now().toString())
            .build();

        log.error("ApiControllerAdvice -> Exception {} " , defaultErrorResponse.toString());

        return new ResponseEntity<>(defaultErrorResponse, e.getHttpStatus());
    }
}
