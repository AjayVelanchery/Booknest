package com.booknest.booknest.controller;

import com.booknest.booknest.dto.AuthResponse;
import com.booknest.booknest.dto.LoginRequest;
import com.booknest.booknest.dto.RegisterRequest;
import com.booknest.booknest.entity.Role;
import com.booknest.booknest.entity.User;
import com.booknest.booknest.repository.UserRepository;
import com.booknest.booknest.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest){
        if (userRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(Collections.singletonMap("message", "Error: Username is already taken!"));
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        userRepository.save(user);

        return ResponseEntity.ok(Collections.singletonMap("message", "User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);


        String username = ((org.springframework.security.core.userdetails.UserDetails)authentication.getPrincipal()).getUsername();


        String jwt = jwtUtils.generateJwtToken(
                (org.springframework.security.core.userdetails.UserDetails)authentication.getPrincipal());

        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}

