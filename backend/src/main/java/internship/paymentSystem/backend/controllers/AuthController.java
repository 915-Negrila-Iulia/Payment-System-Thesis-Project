package internship.paymentSystem.backend.controllers;

import internship.paymentSystem.backend.config.MyLogger;
import internship.paymentSystem.backend.jwt.JwtUtils;
import internship.paymentSystem.backend.DTOs.JwtResponse;
import internship.paymentSystem.backend.DTOs.LoginRequest;
import internship.paymentSystem.backend.DTOs.MessageResponse;
import internship.paymentSystem.backend.DTOs.SignupRequest;
import internship.paymentSystem.backend.services.UserDetailsImpl;
import internship.paymentSystem.backend.services.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class AuthController {

    private final MyLogger LOGGER = MyLogger.getInstance();

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    IUserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        LOGGER.logInfo("HTTP Request -- Post Login Authenticate User");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getRole()));
    }

    @PostMapping("/signup/{currentUserId}")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest, @PathVariable Long currentUserId) {
        LOGGER.logInfo("HTTP Request -- Post Signup Register User");
        userService.signupUser(signUpRequest,currentUserId);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

}