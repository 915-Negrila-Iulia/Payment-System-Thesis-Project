package internship.paymentSystem.backend.services;

import internship.paymentSystem.backend.DTOs.SignupRequest;
import internship.paymentSystem.backend.models.Account;
import internship.paymentSystem.backend.models.Audit;
import internship.paymentSystem.backend.models.User;
import internship.paymentSystem.backend.models.UserHistory;
import internship.paymentSystem.backend.models.enums.ObjectTypeEnum;
import internship.paymentSystem.backend.models.enums.OperationEnum;
import internship.paymentSystem.backend.models.enums.RoleEnum;
import internship.paymentSystem.backend.models.enums.StatusEnum;
import internship.paymentSystem.backend.repositories.IUserRepository;
import internship.paymentSystem.backend.services.interfaces.IAuditService;
import internship.paymentSystem.backend.services.interfaces.IUserHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private IUserRepository userRepository;
    @Mock
    private IUserHistoryService userHistoryService;
    @Mock
    private IAuditService auditService;
    @Mock
    PasswordEncoder encoder;
    @InjectMocks
    private UserService userService;

    User user1, user2, signupUser;
    SignupRequest signupRequest;
    UserHistory signupUserHistory;
    Audit audit;

    @BeforeEach
    public void setup() {
        user1 = new User("bob", "bob@yahoo.com", "", RoleEnum.ADMIN_ROLE,
                StatusEnum.ACTIVE, StatusEnum.ACTIVE);
        user1.setId(1L);

        user2 = new User("mary", "mary@yahoo.com", "", RoleEnum.USER_ROLE,
                StatusEnum.ACTIVE, StatusEnum.ACTIVE);
        user2.setId(2L);

        signupRequest = new SignupRequest("jack","jack@gmail.com","p", RoleEnum.USER_ROLE);
        signupUser = new User("jack", "jack@gmail.com", "2", RoleEnum.USER_ROLE);
        signupUser.setStatus(StatusEnum.APPROVE);
        signupUser.setNextStatus(StatusEnum.ACTIVE);

        signupUserHistory = new UserHistory(signupUser);

        audit = new Audit(signupUser.getId(), ObjectTypeEnum.USER, OperationEnum.CREATE, 1L);
    }

    @Test
    void testGetAllUsers(){
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        when(userRepository.findAll()).thenReturn(users);

        List<User> allUsers = userService.getAllUsers();

        assertEquals(users, allUsers);
        assertEquals(allUsers.size(), 2);

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testFindUserByUsername(){
        when(userRepository.findByUsername("bob")).thenReturn(user1);
        User foundUser = userService.findUserByUsername("bob");
        assertEquals(user1.getUsername(), foundUser.getUsername());
        verify(userRepository, times(1)).findByUsername("bob");
    }

    @Test
    void testSignupUser(){
        Long currentUserId = user1.getId();

        when(userRepository.findById(currentUserId)).thenReturn(Optional.ofNullable(user1));
        when(encoder.encode(signupRequest.getPassword())).thenReturn(String.valueOf(signupRequest.getPassword().charAt(0)%10));
        when(userRepository.save(signupUser)).thenReturn(signupUser);
        when(userHistoryService.saveUserHistory(signupUser)).thenReturn(signupUserHistory);
        when(auditService.saveAudit(audit)).thenReturn(audit);

        userService.signupUser(signupRequest, currentUserId);

        verify(userRepository, times(1)).findById(currentUserId);
        verify(encoder, times(1)).encode(signupRequest.getPassword());
        verify(userRepository, times(1)).save(argThat(user ->
                        user.getUsername().equals(signupUser.getUsername()) &&
                        user.getEmail().equals(signupUser.getEmail()) &&
                        user.getPassword().equals(String.valueOf(signupRequest.getPassword().charAt(0)%10)) &&
                        user.getRole().equals(signupUser.getRole()) &&
                        user.getStatus() == StatusEnum.APPROVE &&
                        user.getNextStatus() == StatusEnum.ACTIVE
        ));
        verify(userHistoryService, times(1)).saveUserHistory(signupUser);
        verify(auditService, times(1)).saveAudit(argThat(audit ->
                        audit.getObjectType() == ObjectTypeEnum.USER &&
                        audit.getOperation() == OperationEnum.CREATE &&
                        audit.getUserID().equals(currentUserId)
        ));
    }
}
