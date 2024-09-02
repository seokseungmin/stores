package com.springboot.stores.service;

import com.springboot.stores.common.exception.BizException;
import com.springboot.stores.common.exception.PasswordNotMatchException;
import com.springboot.stores.common.exception.TokenCreateException;
import com.springboot.stores.common.model.ServiceResult;
import com.springboot.stores.common.model.UserLoginToken;
import com.springboot.stores.dto.UserLoginDto;
import com.springboot.stores.dto.UserRegistrationDto;
import com.springboot.stores.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface UserService {

    ServiceResult registerUser(UserRegistrationDto registrationDto);
    Optional<User> findByUsername(String username);

    UserLoginToken createToken(UserLoginDto userLoginDto) throws PasswordNotMatchException;

    UserLoginToken refreshToken(HttpServletRequest request) throws TokenCreateException;

    User login(UserLoginDto userLoginDto) throws BizException;

}