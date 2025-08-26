package com.EliyatMagar.LearningWebApp.repository;

import com.EliyatMagar.LearningWebApp.model.Course;
import com.EliyatMagar.LearningWebApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByInstructor(User instructor);
    List<Course> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT c FROM Course c JOIN Enrollment e ON c.courseId = e.course.courseId WHERE e.student.userId = :studentId")
    List<Course> findCoursesByStudentId(@Param("studentId") Long studentId);

    Optional<Course> findByCourseIdAndInstructor(Long courseId, User instructor);
    boolean existsByTitleAndInstructor(String title, User instructor);
}