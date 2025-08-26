package com.EliyatMagar.LearningWebApp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CourseRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private Long instructorId; // Optional, usually taken from authenticated user
}