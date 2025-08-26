package com.EliyatMagar.LearningWebApp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class NotesRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private Long courseId;

    // Remove fileUrl and add MultipartFile
    private MultipartFile file;
}