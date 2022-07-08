package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.model.UserHistory;

import java.util.List;
import java.util.Optional;

public interface IUserHistoryService {
    UserHistory saveUserHistory(User user);
    List<UserHistory> getHistoryOfUsers();
}
