package com.springboot.stores.service.Impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.springboot.stores.common.exception.BizException;
import com.springboot.stores.common.exception.PasswordNotMatchException;
import com.springboot.stores.common.exception.TokenCreateException;
import com.springboot.stores.common.exception.UserNotFoundException;
import com.springboot.stores.common.model.ServiceResult;
import com.springboot.stores.common.model.UserLoginToken;
import com.springboot.stores.common.util.JWTUtils;
import com.springboot.stores.common.util.PasswordUtils;
import com.springboot.stores.dto.UserLoginDto;
import com.springboot.stores.dto.UserRegistrationDto;
import com.springboot.stores.entity.User;
import com.springboot.stores.repository.UserRepository;
import com.springboot.stores.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static com.springboot.stores.common.util.PasswordUtils.encryptedPassword;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public ServiceResult registerUser(UserRegistrationDto registrationDto) {

        if (userRepository.countByEmail(registrationDto.getEmail()) > 0) {
            return ServiceResult.fail("이미 존재하는 이메일 입니다.");
        }

        if (userRepository.countByPhone(registrationDto.getPhone()) > 0) {
            return ServiceResult.fail("이미 존재하는 핸드폰 번호입니다.");
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

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * JWT 토큰 발행시 발행 유효기간을 1개월로 저장하는 API.
     */
    @Override
    public UserLoginToken createToken(UserLoginDto userLoginDto) throws PasswordNotMatchException {
        User user = userRepository.findByEmail(userLoginDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("사용자 정보가 없습니다."));

        if (!PasswordUtils.equalPassword(userLoginDto.getPassword(), user.getPassword())) {
            throw new PasswordNotMatchException("비밀번호가 일치하지 않습니다.");
        }

        // 토큰 발행
        return JWTUtils.createToken(user);
    }

    @Override
    public UserLoginToken refreshToken(HttpServletRequest request) throws TokenCreateException {
        String token = request.getHeader("STORE-TOKEN");
        String email = "";

        try {
            email = JWTUtils.getIssuer(token);
        } catch (Exception e) {
            throw new TokenCreateException("토큰 재생성에 실패했습니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("사용자 정보가 없습니다."));

        LocalDateTime expiredDateTime = LocalDateTime.now().plusMonths(1);
        Date expiredDate = java.sql.Timestamp.valueOf(expiredDateTime);

        return JWTUtils.createToken(user);
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
