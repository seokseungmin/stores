package com.springboot.stores.service.Impl;

import com.springboot.stores.common.exception.BizException;
import com.springboot.stores.common.model.ServiceResult;
import com.springboot.stores.common.util.PasswordUtils;
import com.springboot.stores.dto.UserLoginDto;
import com.springboot.stores.dto.UserRegistrationDto;
import com.springboot.stores.entity.User;
import com.springboot.stores.repository.UserRepository;
import com.springboot.stores.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.springboot.stores.common.util.PasswordUtils.encryptedPassword;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public ServiceResult registerUser(UserRegistrationDto registrationDto) {

        if (userRepository.countByEmail(registrationDto.getEmail()) > 0) {
            throw new BizException("이미 존재하는 이메일 입니다.");
        }

        if (userRepository.countByPhone(registrationDto.getPhone()) > 0) {
            throw new BizException("이미 존재하는 존재하는 핸드폰 번호입니다.");
        }

        String encryptPassword = encryptedPassword(registrationDto.getPassword());

        userRepository.save(User.builder()
                .username(registrationDto.getUsername())
                .email(registrationDto.getEmail())
                .phone(registrationDto.getPhone())
                .password(encryptPassword)
                .role(registrationDto.getRole())
                .createdAt(LocalDateTime.now())
                .build());

        return ServiceResult.success("회원가입 성공!");
    }

    @Override
    public User login(UserLoginDto userLoginDto) throws BizException {
        Optional<User> optionalUser = userRepository.findByEmail(userLoginDto.getEmail());
        if (optionalUser.isEmpty()) {
            throw new BizException("회원 정보가 존재하지 않습니다.");
        }

        User user = optionalUser.get();

        if (!PasswordUtils.equalPassword(userLoginDto.getPassword(), user.getPassword())) {
            throw new BizException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }
}
