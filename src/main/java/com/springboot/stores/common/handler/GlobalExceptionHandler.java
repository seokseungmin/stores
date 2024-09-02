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
            ExistsUsernameException.class,
            TokenCreateException.class,
            ExistsEmailException.class,
            PasswordNotMatchException.class,
            UserNotFoundException.class,
            BizException.class,
            NeedParterRoleException.class
    })
    public ResponseEntity<?> handleCustomExceptions(Exception exception) {
        return ResponseResult.fail(exception.getMessage());
    }

    // 다른 글로벌 예외 처리기들이 필요하면 여기에 추가할 수 있습니다.
}
