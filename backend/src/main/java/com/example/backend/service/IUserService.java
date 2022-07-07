package com.example.backend.service;

import com.example.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    User saveUser(User user);
    void deleteUserById(Long id);
    Optional<User> findUserById(Long id);
    User login(String username, String password);
    List<User> getAllUsers();
}
