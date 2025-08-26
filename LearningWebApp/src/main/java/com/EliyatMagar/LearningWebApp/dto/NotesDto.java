package com.EliyatMagar.LearningWebApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotesDto {
    private Long noteId;
    private String title;
    private String fileUrl;
    private LocalDateTime createdAt;
    private Long courseId;
    private String courseTitle;
    private Long uploadedById;
    private String uploadedByName;
}