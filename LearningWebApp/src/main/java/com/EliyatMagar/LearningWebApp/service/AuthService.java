package com.EliyatMagar.LearningWebApp.service;

import com.EliyatMagar.LearningWebApp.dto.request.LoginRequest;
import com.EliyatMagar.LearningWebApp.dto.request.SignupRequest;
import com.EliyatMagar.LearningWebApp.dto.response.JwtResponse;
import com.EliyatMagar.LearningWebApp.dto.response.MessageResponse;
import com.EliyatMagar.LearningWebApp.model.Role;
import com.EliyatMagar.LearningWebApp.model.User;
import com.EliyatMagar.LearningWebApp.repository.UserRepository;
import com.EliyatMagar.LearningWebApp.security.jwt.JwtUtils;
import com.EliyatMagar.LearningWebApp.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Extract role from authorities and convert to our Role enum
        String authority = userDetails.getAuthorities().iterator().next().getAuthority();
        String roleName = authority.replace("ROLE_", "");
        Role userRole = Role.valueOf(roleName);

        return new JwtResponse(jwt,
                userDetails.getUserId(),
                userDetails.getName(),
                userDetails.getEmail(),
                userRole);
    }

    public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Default role to STUDENT if not provided
        Role role = signUpRequest.getRole() != null ? signUpRequest.getRole() : Role.STUDENT;

        // Create new user's account
        User user = User.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .role(role)
                .build();

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}