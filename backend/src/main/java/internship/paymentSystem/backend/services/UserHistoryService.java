package internship.paymentSystem.backend.services;

import internship.paymentSystem.backend.models.User;
import internship.paymentSystem.backend.models.UserHistory;
import internship.paymentSystem.backend.repositories.IUserHistoryRepository;
import internship.paymentSystem.backend.services.interfaces.IUserHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserHistoryService implements IUserHistoryService {

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
                user.getPassword(), user.getStatus(), user.getNextStatus(), user.getId());
        return userHistoryRepository.save(userHistory);
    }

    @Override
    public List<UserHistory> getHistoryByUserId(Long userId){
        return userHistoryRepository.findAll().stream()
                .filter(userHistory -> Objects.equals(userHistory.getUserID(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public UserHistory getLastVersionOfUser(Long userId){
        List<UserHistory> history = this.getHistoryByUserId(userId);
        UserHistory findLastVersion = history.stream().max(Comparator.comparing(UserHistory::getTimestamp)).get();
        UserHistory lastVersion = new UserHistory(findLastVersion.getEmail());
        return lastVersion;
    }

    @Override
    public List<UserHistory> getHistoryOfUsers() {
        return userHistoryRepository.findAll();
    }

    @Override
    public List<UserHistory> getUserState(LocalDateTime timestamp){
        return userHistoryRepository.findAll().stream()
                .filter(user -> user.getTimestamp().isEqual(timestamp)).collect(Collectors.toList());
    }

}
