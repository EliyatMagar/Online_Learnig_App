package com.EliyatMagar.LearningWebApp.dto.response;

import com.EliyatMagar.LearningWebApp.model.Role; // Explicit import
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String name;
    private String email;
    private Role role; // Explicitly uses our Role enum

    // Custom constructor without the 'type' parameter
    public JwtResponse(String token, Long userId, String name, String email, Role role) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}