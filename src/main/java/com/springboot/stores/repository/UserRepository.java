package com.springboot.stores.repository;

import com.springboot.stores.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    int countByEmail(String email);
    int countByPhone(String phone);
    Optional<User> findByEmail(String email);
}