package com.EliyatMagar.LearningWebApp.repository;

import com.EliyatMagar.LearningWebApp.model.Notes;
import com.EliyatMagar.LearningWebApp.model.Course;
import com.EliyatMagar.LearningWebApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotesRepository extends JpaRepository<Notes, Long> {
    List<Notes> findByCourse(Course course);
    List<Notes> findByUploadedBy(User uploadedBy);
    List<Notes> findByCourseAndUploadedBy(Course course, User uploadedBy);

    @Query("SELECT n FROM Notes n WHERE n.course.courseId = :courseId")
    List<Notes> findByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT n FROM Notes n WHERE n.uploadedBy.userId = :userId")
    List<Notes> findByUploadedById(@Param("userId") Long userId);

    Optional<Notes> findByNoteIdAndUploadedBy(Long noteId, User uploadedBy);
}