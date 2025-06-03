package com.dat.backend.datshop.authentication.exception;

import com.dat.backend.datshop.response.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<String> handleAuthenticationException(RuntimeException e) {
        String errorMessage = e.getMessage();
        return ApiResponse.error(400, errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleGeneralException(Exception e) {
        String errorMessage = e.getMessage();
        return ApiResponse.error(500, errorMessage);
    }
}
