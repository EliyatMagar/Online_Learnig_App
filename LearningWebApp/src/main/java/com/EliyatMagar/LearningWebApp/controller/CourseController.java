package com.EliyatMagar.LearningWebApp.controller;

import com.EliyatMagar.LearningWebApp.dto.request.CourseDto;
import com.EliyatMagar.LearningWebApp.dto.request.CourseRequest;
import com.EliyatMagar.LearningWebApp.security.services.UserDetailsImpl;
import com.EliyatMagar.LearningWebApp.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<CourseDto> createCourse(
            @Valid @RequestBody CourseRequest courseRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CourseDto courseDto = courseService.createCourse(courseRequest, userDetails);
        return ResponseEntity.ok(courseDto);
    }

    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        List<CourseDto> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long courseId) {
        CourseDto courseDto = courseService.getCourseById(courseId);
        return ResponseEntity.ok(courseDto);
    }

    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<CourseDto>> getCoursesByInstructor(@PathVariable Long instructorId) {
        List<CourseDto> courses = courseService.getCoursesByInstructor(instructorId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or @courseService.isStudentOrInstructor(authentication.principal.userId, #studentId)")
    public ResponseEntity<List<CourseDto>> getCoursesByStudent(@PathVariable Long studentId) {
        List<CourseDto> courses = courseService.getCoursesByStudent(studentId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CourseDto>> searchCourses(@RequestParam String keyword) {
        List<CourseDto> courses = courseService.searchCourses(keyword);
        return ResponseEntity.ok(courses);
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<CourseDto> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseRequest courseRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CourseDto courseDto = courseService.updateCourse(courseId, courseRequest, userDetails);
        return ResponseEntity.ok(courseDto);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        courseService.deleteCourse(courseId, userDetails);
        return ResponseEntity.noContent().build();
    }
}