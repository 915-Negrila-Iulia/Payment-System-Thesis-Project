package com.example.backend.services.interfaces;

import com.example.backend.models.SignupRequest;
import com.example.backend.models.User;
import com.example.backend.models.UserHistory;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    User saveUser(User user);

    void deleteUserById(Long id);

    Optional<User> findUserById(Long id);

    User findUserByUsername(String username);

    List<User> getAllUsers();

    User undoneUserChanges(User user, UserHistory lastVersion);

    List<UserHistory> getHistoryOfUsers();

    List<UserHistory> getHistoryByUserId(Long userId);

    User updateUser(Long id, Long currentUserId, User userDetails);

    User approveUser(Long id, Long currentUserId);

    User rejectUser(Long id, Long currentUserId);

    User deleteUser(Long id, Long currentUserId);

    void signupUser(SignupRequest signUpRequest,  Long currentUserId);
}
