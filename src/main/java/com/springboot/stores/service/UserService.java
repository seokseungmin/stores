package com.springboot.stores.service;

import com.springboot.stores.common.exception.BizException;
import com.springboot.stores.common.model.ServiceResult;
import com.springboot.stores.dto.UserLoginDto;
import com.springboot.stores.dto.UserRegistrationDto;
import com.springboot.stores.entity.User;

public interface UserService {

    ServiceResult registerUser(UserRegistrationDto registrationDto);

    User login(UserLoginDto userLoginDto) throws BizException;

}