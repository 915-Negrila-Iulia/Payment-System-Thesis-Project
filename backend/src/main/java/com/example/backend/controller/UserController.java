package com.example.backend.controller;

import com.example.backend.model.*;
import com.example.backend.service.IAuditService;
import com.example.backend.service.IUserHistoryService;
import com.example.backend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserHistoryService userHistoryService;

    @Autowired
    private IAuditService auditService;

    @Autowired
    private AuthController authController;

    @GetMapping("/users")
    public List<User> getUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/users/history")
    public List<UserHistory> getHistoryOfUsers() {
        return userHistoryService.getHistoryOfUsers();
    }

    @GetMapping("/users/history/{userId}")
    public List<UserHistory> getHistoryOfUser(@PathVariable Long userId){
        return userHistoryService.getHistoryByUserId(userId);
    }

    @GetMapping("/user-audit/{objectId}/{objectType}")
    public User getUserWhoMadeChanges(@PathVariable Long objectId, @PathVariable ObjectTypeEnum objectType){
        Long userId = auditService.getUserThatMadeUpdates(objectId,objectType);
        return userService.findUserById(userId).get();
    }

    /**
     * Update user
     * Check if the user exists by using the given id
     * And throw an exception otherwise
     * Add a new record in 'UserHistory' table containing the previous state of the user
     * Status of the updated user is changed to 'approve'
     * Update 'Audit' table
     * @param id of the user that is updated
     * @param userDetails updates to be done on the user
     * @return status set to 'OK' and the updated user
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails){
        User user = userService.findUserById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));
        userHistoryService.saveUserHistory(user);
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setStatus(StatusEnum.APPROVE);
        user.setNextStatus(StatusEnum.ACTIVE);
        User updatedUser = userService.saveUser(user);
        Long currentUserId = this.authController.currentUser().getId();
        Audit audit = new Audit(user.getId(),ObjectTypeEnum.USER,OperationEnum.UPDATE,currentUserId);
        auditService.saveAudit(audit);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("users/approve/{id}")
    public ResponseEntity<User> approveUser(@PathVariable Long id){
        User user = userService.findUserById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));
        userHistoryService.saveUserHistory(user);
        user.setStatus(StatusEnum.ACTIVE);
        user.setNextStatus(StatusEnum.ACTIVE);
        User activeUser = userService.saveUser(user);
        Long currentUserId = this.authController.currentUser().getId();
        Audit audit = new Audit(user.getId(),ObjectTypeEnum.USER,OperationEnum.APPROVE,currentUserId);
        auditService.saveAudit(audit);
        return ResponseEntity.ok(activeUser);
    }

    @PutMapping("users/reject/{id}")
    public ResponseEntity<User> rejectUser(@PathVariable Long id){
        User user = userService.findUserById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));
        UserHistory lastVersion = userHistoryService.getLastVersionOfUser(id);
        userHistoryService.saveUserHistory(user);
        if(userHistoryService.getHistoryByUserId(id).size() <= 2){
            // user will be deleted
            user.setStatus(StatusEnum.DELETE);
            user.setNextStatus(StatusEnum.DELETE);
        }
        else{
            // user will have changes undone
            userService.undoneUserChanges(user,lastVersion);
            System.out.println(lastVersion.getEmail());
        }
        User rejectedUser = userService.saveUser(user);
        Long currentUserId = this.authController.currentUser().getId();
        Audit audit = new Audit(user.getId(),ObjectTypeEnum.USER,OperationEnum.APPROVE,currentUserId);
        auditService.saveAudit(audit);
        return ResponseEntity.ok(rejectedUser);
    }

    //todo: delete user => inactive
}
