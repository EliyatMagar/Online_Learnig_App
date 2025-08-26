package com.EliyatMagar.LearningWebApp.service;

import com.EliyatMagar.LearningWebApp.dto.request.CourseDto;
import com.EliyatMagar.LearningWebApp.dto.request.CourseRequest;
import com.EliyatMagar.LearningWebApp.model.Course;
import com.EliyatMagar.LearningWebApp.model.User;
import com.EliyatMagar.LearningWebApp.repository.CourseRepository;
import com.EliyatMagar.LearningWebApp.repository.UserRepository;
import com.EliyatMagar.LearningWebApp.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseDto createCourse(CourseRequest courseRequest, UserDetailsImpl currentUser) {
        User instructor = userRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is instructor or admin
        if (!currentUser.getAuthorities().stream().anyMatch(auth ->
                auth.getAuthority().equals("ROLE_INSTRUCTOR") || auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only instructors can create courses");
        }

        // Check for duplicate course title for this instructor
        if (courseRepository.existsByTitleAndInstructor(courseRequest.getTitle(), instructor)) {
            throw new RuntimeException("Course with this title already exists");
        }

        Course course = Course.builder()
                .title(courseRequest.getTitle())
                .description(courseRequest.getDescription())
                .instructor(instructor)
                .build();

        Course savedCourse = courseRepository.save(course);
        return mapToCourseDto(savedCourse);
    }

    public List<CourseDto> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream().map(this::mapToCourseDto).collect(Collectors.toList());
    }

    public CourseDto getCourseById(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        return mapToCourseDto(course);
    }

    public List<CourseDto> getCoursesByInstructor(Long instructorId) {
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        List<Course> courses = courseRepository.findByInstructor(instructor);
        return courses.stream().map(this::mapToCourseDto).collect(Collectors.toList());
    }

    public List<CourseDto> getCoursesByStudent(Long studentId) {
        List<Course> courses = courseRepository.findCoursesByStudentId(studentId);
        return courses.stream().map(this::mapToCourseDto).collect(Collectors.toList());
    }

    public List<CourseDto> searchCourses(String keyword) {
        List<Course> courses = courseRepository.findByTitleContainingIgnoreCase(keyword);
        return courses.stream().map(this::mapToCourseDto).collect(Collectors.toList());
    }

    public CourseDto updateCourse(Long courseId, CourseRequest courseRequest, UserDetailsImpl currentUser) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if current user is the course instructor or admin
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isInstructor = course.getInstructor().getUserId().equals(currentUser.getUserId());

        if (!isAdmin && !isInstructor) {
            throw new AccessDeniedException("You are not authorized to update this course");
        }

        course.setTitle(courseRequest.getTitle());
        course.setDescription(courseRequest.getDescription());

        Course updatedCourse = courseRepository.save(course);
        return mapToCourseDto(updatedCourse);
    }

    public void deleteCourse(Long courseId, UserDetailsImpl currentUser) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if current user is the course instructor or admin
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isInstructor = course.getInstructor().getUserId().equals(currentUser.getUserId());

        if (!isAdmin && !isInstructor) {
            throw new AccessDeniedException("You are not authorized to delete this course");
        }

        courseRepository.delete(course);
    }

    private CourseDto mapToCourseDto(Course course) {
        return CourseDto.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .description(course.getDescription())
                .instructorId(course.getInstructor().getUserId())
                .instructorName(course.getInstructor().getName())
                .build();
    }
}