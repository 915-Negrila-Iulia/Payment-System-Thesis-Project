package com.example.backend.services;

import com.example.backend.models.enumerations.StatusEnum;
import com.example.backend.models.User;
import com.example.backend.models.UserHistory;
import com.example.backend.repositories.IUserRepository;
import com.example.backend.services.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

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
        User user = userRepository.findByUsername(username);
        return user;
    }

    @Override
    public User undoneUserChanges(User user, UserHistory lastVersion){
        user.setEmail(lastVersion.getEmail());
        user.setStatus(StatusEnum.ACTIVE);
        user.setNextStatus(StatusEnum.ACTIVE);
        return user;
    }
}
