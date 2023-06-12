package internship.paymentSystem.backend.services.interfaces;

import internship.paymentSystem.backend.DTOs.SignupRequest;
import internship.paymentSystem.backend.models.User;
import internship.paymentSystem.backend.models.UserHistory;

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

    User approveUser(Long id, Long currentUserId) throws Exception;

    User rejectUser(Long id, Long currentUserId) throws Exception;

    User deleteUser(Long id, Long currentUserId);

    boolean isUserAdmin(Long id);

    void signupUser(SignupRequest signUpRequest, Long currentUserId);
}
