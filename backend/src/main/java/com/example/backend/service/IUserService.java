package com.example.backend.service;

import com.example.backend.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    User saveUser(User user);
    void deleteUserById(Long id);
    Optional<User> findUserById(Long id);
    User findUserByUsername(String username);
    List<User> getAllUsers();
}
