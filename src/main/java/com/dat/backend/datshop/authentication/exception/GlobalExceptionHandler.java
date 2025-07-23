package com.dat.backend.datshop.authentication.exception;

import com.dat.backend.datshop.template.ApiResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<String> handleAuthenticationException(RuntimeException e) {
        String errorMessage = e.getMessage();
        return ApiResponse.error(400, errorMessage);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ApiResponse<String> handleBadCredentialsException(BadCredentialsException e) {
        return ApiResponse.error(401, "Invalid username or password");
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ApiResponse<String> handleNotAuthorizedException(NotAuthorizedException e) {
        String errorMessage = e.getMessage();
        return ApiResponse.error(403, errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleGeneralException(Exception e) {
        String errorMessage = e.getMessage();
        return ApiResponse.error(500, errorMessage);
    }
}
