package com.springboot.stores.common.handler;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.springboot.stores.common.exception.*;
import com.springboot.stores.common.model.ResponseResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            JWTVerificationException.class,
            BizException.class
    })
    public ResponseEntity<?> handleCustomExceptions(Exception exception) {
        return ResponseResult.fail(exception.getMessage());
    }
}
