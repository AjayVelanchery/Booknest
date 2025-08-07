package com.booknest.booknest.service;

import com.booknest.booknest.dto.ApiResponse;
import com.booknest.booknest.dto.AuthResponse;
import com.booknest.booknest.dto.LoginRequest;
import com.booknest.booknest.dto.RegisterRequest;
import com.booknest.booknest.entity.Role;
import com.booknest.booknest.entity.User;
import com.booknest.booknest.repository.UserRepository;
import com.booknest.booknest.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtUtils jwtUtils,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    private boolean isPasswordStrong(String password) {
        if (password == null) return false;

        if (password.length() < 8) return false;

        boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));

        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }





    public ResponseEntity<ApiResponse> registerUser(RegisterRequest signUpRequest) {

        if (userRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "Error: Username is already taken!"));
        }


        if (!isPasswordStrong(signUpRequest.getPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false,
                            "Error: Password must be at least 8 characters long and include " +
                                    "uppercase, lowercase, digit, and special character."));
        }


        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully!"));
    }






    public ResponseEntity<AuthResponse> authenticateUser(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);


        String jwt = jwtUtils.generateJwtToken(
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal()
        );

        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
