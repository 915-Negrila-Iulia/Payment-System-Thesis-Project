package com.example.backend.services.interfaces;

import com.example.backend.models.User;
import com.example.backend.models.UserHistory;

import java.util.List;

public interface IUserHistoryService {

    UserHistory saveUserHistory(User user);

    List<UserHistory> getHistoryOfUsers();

    List<UserHistory> getHistoryByUserId(Long id);

    UserHistory getLastVersionOfUser(Long userId);

}
