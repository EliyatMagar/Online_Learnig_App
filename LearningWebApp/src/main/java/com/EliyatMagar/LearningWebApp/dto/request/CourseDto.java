package com.EliyatMagar.LearningWebApp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
    private Long courseId;
    private String title;
    private String description;
    private Long instructorId;
    private String instructorName;
}