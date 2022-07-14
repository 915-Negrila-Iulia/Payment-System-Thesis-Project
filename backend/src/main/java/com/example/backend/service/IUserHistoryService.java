package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.model.UserHistory;

import java.util.List;

public interface IUserHistoryService {
    UserHistory saveUserHistory(User user);
    List<UserHistory> getHistoryOfUsers();
    List<UserHistory> getHistoryByUserId(Long id);
    UserHistory getLastVersionOfUser(Long userId);
}
