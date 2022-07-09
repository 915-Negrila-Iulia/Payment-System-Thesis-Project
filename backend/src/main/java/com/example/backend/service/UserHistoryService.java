package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.model.UserHistory;
import com.example.backend.repository.IUserHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        UserHistory userHistory = new UserHistory(user.getUsername(),user.getEmail(),
                user.getPassword(), user.getStatus(), user.getId());
        return userHistoryRepository.save(userHistory);
    }

    @Override
    public List<UserHistory> getHistoryOfUsers() {
        return userHistoryRepository.findAll();
    }
}
