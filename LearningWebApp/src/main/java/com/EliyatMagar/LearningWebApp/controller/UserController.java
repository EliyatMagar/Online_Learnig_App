package com.EliyatMagar.LearningWebApp.controller;

import com.EliyatMagar.LearningWebApp.dto.request.UserDto;
import com.EliyatMagar.LearningWebApp.dto.request.UserUpdateRequest;
import com.EliyatMagar.LearningWebApp.model.Role;
import com.EliyatMagar.LearningWebApp.security.services.UserDetailsImpl;
import com.EliyatMagar.LearningWebApp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Get current user's profile - Available to all authenticated users
    // Uses data from the token (UserDetailsImpl) for efficiency, no DB call needed!
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserDto userDto = userService.mapUserDetailsToDto(userDetails);
        return ResponseEntity.ok(userDto);
    }

    // Get any user by ID - Admin only (requires DB fetch)
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(userDto);
    }

    // Get all users - Admin only (requires DB fetch)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Get all users by role - Admin only (requires DB fetch)
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getUsersByRole(@PathVariable Role role) {
        List<UserDto> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    // Update a user - User can update their own profile, Admin can update any profile
    // Requires DB fetch for the user being updated
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest updateRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails) { // Injected by Spring Security

        UserDto updatedUser = userService.updateUser(userId, updateRequest, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    // Delete a user - Admin only (operates on DB)
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}