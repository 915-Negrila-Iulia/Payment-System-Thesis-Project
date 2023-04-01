package internship.paymentSystem.backend.controllers;

import internship.paymentSystem.backend.DTOs.BaseObjectDto;
import internship.paymentSystem.backend.DTOs.CurrentUserDto;
import internship.paymentSystem.backend.config.MyLogger;
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

    private final MyLogger LOGGER = MyLogger.getInstance();

    @Autowired
    private IUserService userService;

    @GetMapping("/users")
    public List<User> getUsers() {
        LOGGER.logInfo("HTTP Request -- Get Users");
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        LOGGER.logInfo("HTTP Request -- Get User by Id");
        return userService.findUserById(id).isPresent() ? userService.findUserById(id).get() : null;
    }

    @GetMapping("/users/history")
    public List<UserHistory> getHistoryOfUsers() {
        LOGGER.logInfo("HTTP Request -- Get History of Users");
        return userService.getHistoryOfUsers();
    }

    @GetMapping("/users/history/{userId}")
    public List<UserHistory> getHistoryOfUser(@PathVariable Long userId){
        LOGGER.logInfo("HTTP Request -- Get History of Given User");
        return userService.getHistoryByUserId(userId);
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody BaseObjectDto<User> userDto){
        try{
            User updatedUser = this.userService.updateUser(userDto.getCurrentUserDto().getObjectId(),
                    userDto.getCurrentUserDto().getCurrentUserId(), userDto.getObject());
            LOGGER.logInfo("HTTP Request -- Put Update User");
            return ResponseEntity.ok(updatedUser);
        }
        catch(Exception e){
            LOGGER.logError("HTTP Request -- Put Update User Failed: "+e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("users/approve")
    public ResponseEntity<?> approveUser(@RequestBody CurrentUserDto currentUserDto){
        try {
            User activeUser = userService.approveUser(currentUserDto.getObjectId(), currentUserDto.getCurrentUserId());
            LOGGER.logInfo("HTTP Request -- Put Approve User");
            return ResponseEntity.ok(activeUser);
        }
        catch(Exception e){
            LOGGER.logError("HTTP Request -- Put Approve User Failed: "+e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("users/reject")
    public ResponseEntity<?> rejectUser(@RequestBody CurrentUserDto currentUserDto){
        try {
            User rejectedUser = userService.rejectUser(currentUserDto.getObjectId(), currentUserDto.getCurrentUserId());
            LOGGER.logInfo("HTTP Request -- Put Reject User");
            return ResponseEntity.ok(rejectedUser);
        }
        catch(Exception e){
            LOGGER.logError("HTTP Request -- Put Reject User Failed: "+e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("users/delete")
    public ResponseEntity<User> deleteUser(@RequestBody CurrentUserDto currentUserDto){
        try {
            User deletedUser = userService.deleteUser(currentUserDto.getObjectId(), currentUserDto.getCurrentUserId());
            LOGGER.logInfo("HTTP Request -- Put Delete User");
            return ResponseEntity.ok(deletedUser);
        }
        catch(Exception e){
            LOGGER.logError("HTTP Request -- Put Delete User Failed: "+e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
