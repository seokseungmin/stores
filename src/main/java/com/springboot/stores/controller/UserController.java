package com.springboot.stores.controller;

import com.springboot.stores.common.exception.BizException;
import com.springboot.stores.common.model.ResponseError;
import com.springboot.stores.common.model.ResponseResult;
import com.springboot.stores.common.model.ServiceResult;
import com.springboot.stores.common.model.UserLoginToken;
import com.springboot.stores.common.util.JWTUtils;
import com.springboot.stores.dto.UserLoginDto;
import com.springboot.stores.dto.UserRegistrationDto;
import com.springboot.stores.entity.User;
import com.springboot.stores.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    //사용자 등록 API, 역할이 파트너인지, 유저인지
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegistrationDto registrationDto, Errors errors) {

        List<ResponseError> responseErrorList = new ArrayList<>();

        if (errors.hasErrors()) {
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });

            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        ServiceResult serviceResult = userService.registerUser(registrationDto);

        return ResponseResult.result(serviceResult);
    }

    // API 로그인, 로그인시 토큰 발급됨.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginDto userLoginDto, Errors errors) {

        List<ResponseError> responseErrorList = new ArrayList<>();
        if (errors.hasErrors()) {
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }


        User user = null;

        try {
            user = userService.login(userLoginDto);
        } catch (BizException e) {
            return ResponseResult.fail(e.getMessage());
        }

        UserLoginToken userLoginToken = JWTUtils.createToken(user);

        if (userLoginToken == null) {
            return ResponseResult.fail("토큰 생성에 실패하였습니다.");
        }

        return ResponseResult.success("로그인 성공!", userLoginToken);
    }

//    @PostMapping("/createToken")
//    public ResponseEntity<?> createToken(@RequestBody @Valid UserLoginDto userLoginDto, Errors errors) {
//
//        List<ResponseError> responseErrorList = new ArrayList<>();
//        if (errors.hasErrors()) {
//            errors.getAllErrors().forEach((e) -> {
//                responseErrorList.add(ResponseError.of((FieldError) e));
//            });
//            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
//        }
//
//        UserLoginToken userLoginToken = userService.createToken(userLoginDto);
//
//        return ResponseEntity.ok().body(UserLoginToken.builder().token(userLoginToken.getToken()).build());
//    }
//
//    @PatchMapping("/refreshToken")
//    public ResponseEntity<?> refreshToken(HttpServletRequest request) throws TokenCreateException {
//
//        try {
//            UserLoginToken userRefreshToken = userService.refreshToken(request);
//            return ResponseEntity.ok().body(UserLoginToken.builder().token(userRefreshToken.getToken()).build());
//        } catch (Exception e){
//            return ResponseEntity.ok().body(ResponseResult.fail(e.getMessage()));
//        }
//    }

}