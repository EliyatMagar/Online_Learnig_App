package com.EliyatMagar.LearningWebApp.dto.request;

import com.EliyatMagar.LearningWebApp.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long userId;
    private String name;
    private String email;
    private Role role; // This MUST be your custom Role enum
}