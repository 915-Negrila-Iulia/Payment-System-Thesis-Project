package internship.paymentSystem.backend.services.interfaces;

import internship.paymentSystem.backend.models.User;
import internship.paymentSystem.backend.models.UserHistory;

import java.util.List;

public interface IUserHistoryService {

    UserHistory saveUserHistory(User user);

    List<UserHistory> getHistoryOfUsers();

    List<UserHistory> getHistoryByUserId(Long id);

    UserHistory getLastVersionOfUser(Long userId);

}
