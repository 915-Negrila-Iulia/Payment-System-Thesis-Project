package com.example.backend.controllers;

import com.example.backend.models.*;
import com.example.backend.services.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/users")
    public List<User> getUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {return userService.findUserById(id).get();}

    @GetMapping("/users/history")
    public List<UserHistory> getHistoryOfUsers() {
        return userService.getHistoryOfUsers();
    }

    @GetMapping("/users/history/{userId}")
    public List<UserHistory> getHistoryOfUser(@PathVariable Long userId){
        return userService.getHistoryByUserId(userId);
    }

    @PutMapping("/users/{id}/{currentUserId}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @PathVariable Long currentUserId, @RequestBody User userDetails){
        User updatedUser = this.userService.updateUser(id,currentUserId,userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("users/approve/{id}/{currentUserId}")
    public ResponseEntity<User> approveUser(@PathVariable Long id, @PathVariable Long currentUserId){
        User activeUser = userService.approveUser(id,currentUserId);
        return ResponseEntity.ok(activeUser);
    }

    @PutMapping("users/reject/{id}/{currentUserId}")
    public ResponseEntity<User> rejectUser(@PathVariable Long id, @PathVariable Long currentUserId){
        User rejectedUser = userService.rejectUser(id,currentUserId);
        return ResponseEntity.ok(rejectedUser);
    }

    @PutMapping("users/delete/{id}/{currentUserId}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id, @PathVariable Long currentUserId){
        User deletedUser = userService.deleteUser(id,currentUserId);
        return ResponseEntity.ok(deletedUser);
    }
}
