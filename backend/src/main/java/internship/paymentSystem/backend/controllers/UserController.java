package internship.paymentSystem.backend.controllers;

import internship.paymentSystem.backend.DTOs.BaseObjectDto;
import internship.paymentSystem.backend.DTOs.CurrentUserDto;
import internship.paymentSystem.backend.services.interfaces.IUserService;
import internship.paymentSystem.backend.models.User;
import internship.paymentSystem.backend.models.UserHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
//@CrossOrigin(origins = "http://frontend-paymentsys.s3-website-eu-west-1.amazonaws.com")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/users")
    public List<User> getUsers() {
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

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody BaseObjectDto<User> userDto){
        User updatedUser = this.userService.updateUser(userDto.getCurrentUserDto().getObjectId(),
                userDto.getCurrentUserDto().getCurrentUserId(), userDto.getObject());
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("users/approve")
    public ResponseEntity<?> approveUser(@RequestBody CurrentUserDto currentUserDto){
        try {
            User activeUser = userService.approveUser(currentUserDto.getObjectId(), currentUserDto.getCurrentUserId());
            return ResponseEntity.ok(activeUser);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("users/reject")
    public ResponseEntity<?> rejectUser(@RequestBody CurrentUserDto currentUserDto){
        try {
            User rejectedUser = userService.rejectUser(currentUserDto.getObjectId(), currentUserDto.getCurrentUserId());
            return ResponseEntity.ok(rejectedUser);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("users/delete")
    public ResponseEntity<User> deleteUser(@RequestBody CurrentUserDto currentUserDto){
        User deletedUser = userService.deleteUser(currentUserDto.getObjectId(), currentUserDto.getCurrentUserId());
        return ResponseEntity.ok(deletedUser);
    }
}
