package com.springboot.stores.dto;

import com.springboot.stores.entity.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {

    @NotBlank(message = "이름은 필수 항목 입니다.")
    private String username;

    @Email(message = "이메일 형식에 맞게 입력해 주세요.")
    @NotBlank(message = "이메일은 필수 항목 입니다.")
    private String email;

    @Size(min = 4, message = "비밀번호는 4자 이상 입력해야 합니다.")
    @NotBlank(message = "비밀번호는 필수 항목 입니다.")
    private String password;

    @Size(max = 20, message = "연락처는 최대 20자까지 입력해야 합니다.")
    @NotBlank(message = "연락처는 필수 항목 입니다.")
    private String phone;

    @Enumerated(EnumType.STRING) // Enum을 문자열로 저장
    @NotNull // Enum 값이 null이면 안됨
    private Role role;
}
