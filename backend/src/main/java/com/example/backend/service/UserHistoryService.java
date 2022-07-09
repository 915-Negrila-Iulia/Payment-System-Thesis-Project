package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.model.UserHistory;
import com.example.backend.repository.IUserHistoryRepository;
import com.example.backend.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserHistoryService implements IUserHistoryService{

    @Autowired
    private IUserHistoryRepository userHistoryRepository;

    /**
     * Create a new UserHistory object
     * Save version of a given user in the 'UserHistory' table
     * @param user details of UserHistory object
     * @return UserHistory object created
     */
    @Override
    public UserHistory saveUserHistory(User user) {
        UserHistory userHistory = new UserHistory();
        userHistory.setUsername(user.getUsername());
        userHistory.setEmail(user.getEmail());
        userHistory.setPassword(user.getPassword());
        userHistory.setStatus(user.getStatus());
        userHistory.setUserID(user.getId());
        return userHistoryRepository.save(userHistory);
    }

    @Override
    public List<UserHistory> getHistoryOfUsers() {
        return userHistoryRepository.findAll();
    }
}
