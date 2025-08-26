package com.EliyatMagar.LearningWebApp.service;

import com.EliyatMagar.LearningWebApp.dto.request.UserDto;
import com.EliyatMagar.LearningWebApp.dto.request.UserUpdateRequest;
import com.EliyatMagar.LearningWebApp.model.User;
import com.EliyatMagar.LearningWebApp.model.Role;
import com.EliyatMagar.LearningWebApp.repository.UserRepository;
import com.EliyatMagar.LearningWebApp.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Get any user's profile by ID (Admin only)
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return mapToUserDto(user);
    }

    // Get all users (Admin only)
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::mapToUserDto).collect(Collectors.toList());
    }

    // Get all users by role (e.g., all instructors)
    public List<UserDto> getUsersByRole(Role role) {
        List<User> users = userRepository.findByRole(role);
        return users.stream().map(this::mapToUserDto).collect(Collectors.toList());
    }

    // Update a user
    public UserDto updateUser(Long userId, UserUpdateRequest updateRequest, UserDetailsImpl currentUserDetails) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        // Authorization Check
        boolean isAdmin = currentUserDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwnProfile = currentUserDetails.getUserId().equals(userId);

        if (!isAdmin && !isOwnProfile) {
            throw new AccessDeniedException("You are not authorized to update this user's profile.");
        }

        // Apply updates
        if (updateRequest.getName() != null) {
            userToUpdate.setName(updateRequest.getName());
        }
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(userToUpdate.getEmail())) {
            if (userRepository.existsByEmail(updateRequest.getEmail())) {
                throw new RuntimeException("Email is already in use.");
            }
            userToUpdate.setEmail(updateRequest.getEmail());
        }
        // Only an ADMIN can change a user's role
        if (updateRequest.getRole() != null && isAdmin) {
            userToUpdate.setRole(updateRequest.getRole()); // This should work now with correct types
        }

        User savedUser = userRepository.save(userToUpdate);
        return mapToUserDto(savedUser);
    }

    // Delete a user (Admin only)
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UsernameNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    // Helper method to convert User entity to UserDto
    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole()) // This should work now with correct types
                .build();
    }

    // Helper to convert UserDetailsImpl to a simple DTO (for /me endpoint)
    public UserDto mapUserDetailsToDto(UserDetailsImpl userDetails) {
        return UserDto.builder()
                .userId(userDetails.getUserId())
                .name(userDetails.getName())
                .email(userDetails.getEmail())
                .role(extractRoleFromAuthority(userDetails)) // This should work now with correct types
                .build();
    }

    // Helper to extract the Role enum from the UserDetailsImpl authorities
    private Role extractRoleFromAuthority(UserDetailsImpl userDetails) {
        String roleString = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_STUDENT")
                .replace("ROLE_", "");

        // Convert the String to your custom Role enum
        return Role.valueOf(roleString);
    }
}