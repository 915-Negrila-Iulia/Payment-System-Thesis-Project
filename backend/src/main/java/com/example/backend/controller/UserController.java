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
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public List<User> getUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/users/history")
    public List<UserHistory> getHistoryOfUsers() {
        return userHistoryService.getHistoryOfUsers();
    }

    /**
     * Register user
     * Create new user and save it
     * Encode user's password before storing it in the database
     * Update audit
     * @param user user to be created
     * @return created user
     */
    @PostMapping("/register")
    public User createUser(@RequestBody User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User createdUser = userService.saveUser(user);
        Audit audit = new Audit();
        audit.setObjectID(user.getId());
        audit.setObjectType(ObjectTypeEnum.USER);
        audit.setOperation(OperationEnum.CREATE);
        audit.setUserID(0L); //todo: remove hardcoded id; make sessions for users
        auditService.saveAudit(audit);
        return createdUser;
    }

    /**
     * Login user
     * Check if the given user exists in the database
     * And return null if not found
     * @param user user to be checked
     * @return status set to 'OK' and the authenticated user
     */
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user){
        User authUser = userService.findUserByUsername(user.getUsername());
        if(passwordEncoder.matches(user.getPassword(),authUser.getPassword())){
            return ResponseEntity.ok(authUser);
        }
        else {
            return null;
        }
    }

    /**
     * Update user
     * Check if the user exists by using the given id
     * And throw an exception otherwise
     * Add a new record in 'UserHistory' table containing the previous state of the user
     * Status of the updated user is changed to 'approve'
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
        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        user.setStatus(StatusEnum.APPROVE);
        User updatedUser = userService.saveUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("users/approve/{id}")
    public ResponseEntity<User> approveUser(@PathVariable Long id){
        User user = userService.findUserById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));
        userHistoryService.saveUserHistory(user);
        user.setStatus(StatusEnum.ACTIVE);
        User activeUser = userService.saveUser(user);
        return ResponseEntity.ok(activeUser);
    }

    //todo: delete user => inactive
}
