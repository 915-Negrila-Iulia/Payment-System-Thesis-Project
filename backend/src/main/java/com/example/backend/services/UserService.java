package com.example.backend.services;

import com.example.backend.models.*;
import com.example.backend.models.enumerations.ObjectTypeEnum;
import com.example.backend.models.enumerations.OperationEnum;
import com.example.backend.models.enumerations.StatusEnum;
import com.example.backend.repositories.IUserRepository;
import com.example.backend.services.interfaces.IAuditService;
import com.example.backend.services.interfaces.IUserHistoryService;
import com.example.backend.services.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IUserHistoryService userHistoryService;

    @Autowired
    private IAuditService auditService;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User findUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Override
    public User undoneUserChanges(User user, UserHistory lastVersion){
        user.setEmail(lastVersion.getEmail());
        user.setStatus(StatusEnum.ACTIVE);
        user.setNextStatus(StatusEnum.ACTIVE);
        return user;
    }

    @Override
    public List<UserHistory> getHistoryOfUsers() {
        return userHistoryService.getHistoryOfUsers();
    }

    @Override
    public List<UserHistory> getHistoryByUserId(Long userId) {
        return userHistoryService.getHistoryByUserId(userId);
    }

    /**
     * Create user
     * Add a new record in 'UserHistory' table containing the initial state of the user
     * Add a new record in 'User' table
     * Change 'Status' to 'APPROVE' and 'NextStatus' to 'ACTIVE'
     * Update 'Audit' table
     * @param signUpRequest details of user that is created
     * @param currentUserId id of user performing the registration
     */
    @Override
    public void signupUser(SignupRequest signUpRequest, Long currentUserId) {
        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                StatusEnum.APPROVE,
                StatusEnum.ACTIVE);
        userRepository.save(user);
        userHistoryService.saveUserHistory(user);
        Audit audit = new Audit(user.getId(), ObjectTypeEnum.USER, OperationEnum.CREATE,currentUserId);
        auditService.saveAudit(audit);
    }

    /**
     * Update user
     * Check if the user exists by using the given id
     * And throw an exception otherwise
     * Add a new record in 'UserHistory' table containing the previous state of the user
     * Change 'Status' to 'APPROVE' and 'NextStatus' to 'ACTIVE'
     * Update 'Audit' table
     * @param id of the user that is updated
     * @param currentUserId id of user performing the update
     * @param userDetails updates to be done on the user
     * @return the updated user
     */
    @Override
    public User updateUser(Long id, Long currentUserId, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));
        this.userHistoryService.saveUserHistory(user);
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setStatus(StatusEnum.APPROVE);
        user.setNextStatus(StatusEnum.ACTIVE);
        User updatedUser = userRepository.save(user);
        Audit audit = new Audit(user.getId(), ObjectTypeEnum.USER, OperationEnum.UPDATE,currentUserId);
        auditService.saveAudit(audit);
        return updatedUser;
    }

    /**
     * Approve user's changes
     * Check if the user exists by using the given id and throw an exception otherwise
     * Check if user that wants to approve changes is not the same one that made them previously
     * Add a new record in 'UserHistory' table containing the previous state of the user
     * Change 'Status' to 'ACTIVE' and 'NextStatus' to 'ACTIVE'
     * Update 'Audit' table
     * @param id of the user that is approved
     * @param currentUserId id of user performing the approval
     * @return the approved user
     */
    @Override
    public User approveUser(Long id, Long currentUserId) {
        if(!Objects.equals(auditService.getUserThatMadeUpdates(id, ObjectTypeEnum.USER), currentUserId)) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));
            userHistoryService.saveUserHistory(user);
            user.setStatus(user.getNextStatus());
            User activeUser = userRepository.save(user);
            Audit audit = new Audit(user.getId(), ObjectTypeEnum.USER, OperationEnum.APPROVE, currentUserId);
            auditService.saveAudit(audit);
            return activeUser;
        }
        return null;
    }

    /**
     * Reject user's changes
     * Check if the user exists by using the given id and throw an exception otherwise
     * Check if user that wants to reject changes is not the same one that made them previously
     * Add a new record in 'UserHistory' table containing the previous state of the user
     * Change 'Status' to 'ACTIVE' and 'NextStatus' to 'ACTIVE'
     * Update 'Audit' table
     * @param id of the user that is rejected
     * @param currentUserId id of user performing the rejection
     * @return the rejected user
     */
    @Override
    public User rejectUser(Long id, Long currentUserId) {
        if(!Objects.equals(auditService.getUserThatMadeUpdates(id, ObjectTypeEnum.USER), currentUserId)) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));
            UserHistory lastVersion = userHistoryService.getLastVersionOfUser(id);
            userHistoryService.saveUserHistory(user);
            if (userHistoryService.getHistoryByUserId(id).size() <= 2) {
                // user will be deleted
                user.setStatus(StatusEnum.DELETE);
                user.setNextStatus(StatusEnum.DELETE);
            } else {
                // user will have changes undone
                this.undoneUserChanges(user, lastVersion);
                System.out.println(lastVersion.getEmail());
            }
            User rejectedUser = userRepository.save(user);
            Audit audit = new Audit(user.getId(), ObjectTypeEnum.USER, OperationEnum.REJECT, currentUserId);
            auditService.saveAudit(audit);
            return rejectedUser;
        }
        return null;
    }

    /**
     * Delete user
     * User object remains stored in the database
     * Check if the user exists by using the given id
     * And throw an exception otherwise
     * Add a new record in 'UserHistory' table containing the previous state of the user
     * Change 'Status' and 'NextStatus' to 'DELETE'
     * Update 'Audit' table
     * @param id of the user that is deleted
     * @param currentUserId id of user performing the deletion
     * @return the deleted user
     */
    @Override
    public User deleteUser(Long id, Long currentUserId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));
        userHistoryService.saveUserHistory(user);
        user.setStatus(StatusEnum.APPROVE);
        user.setNextStatus(StatusEnum.DELETE);
        User deletedUser = userRepository.save(user);
        Audit audit = new Audit(user.getId(),ObjectTypeEnum.USER,OperationEnum.DELETE,currentUserId);
        auditService.saveAudit(audit);
        return deletedUser;
    }

}
