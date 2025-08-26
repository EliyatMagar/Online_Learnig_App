package com.EliyatMagar.LearningWebApp.service;

import com.EliyatMagar.LearningWebApp.dto.NotesDto;
import com.EliyatMagar.LearningWebApp.dto.request.NotesRequest;
import com.EliyatMagar.LearningWebApp.model.Notes;
import com.EliyatMagar.LearningWebApp.model.Course;
import com.EliyatMagar.LearningWebApp.model.User;
import com.EliyatMagar.LearningWebApp.repository.NotesRepository;
import com.EliyatMagar.LearningWebApp.repository.CourseRepository;
import com.EliyatMagar.LearningWebApp.repository.UserRepository;
import com.EliyatMagar.LearningWebApp.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotesService {

    private final NotesRepository notesRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public NotesDto createNote(NotesRequest notesRequest, UserDetailsImpl currentUser) {
        try {
            User uploadedBy = userRepository.findById(currentUser.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Course course = courseRepository.findById(notesRequest.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            // Check if user is instructor of the course or admin
            boolean isAdmin = currentUser.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            boolean isCourseInstructor = course.getInstructor().getUserId().equals(currentUser.getUserId());

            if (!isAdmin && !isCourseInstructor) {
                throw new AccessDeniedException("Only course instructors can upload notes");
            }

            // Store the file and get the generated filename
            String storedFilename = fileStorageService.storeFile(notesRequest.getFile());
            String fileUrl = fileStorageService.getFileUrl(storedFilename);

            Notes note = Notes.builder()
                    .title(notesRequest.getTitle())
                    .fileUrl(fileUrl)
                    .createdAt(LocalDateTime.now())
                    .course(course)
                    .uploadedBy(uploadedBy)
                    .build();

            Notes savedNote = notesRepository.save(note);
            return mapToNotesDto(savedNote);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create note: " + e.getMessage());
        }
    }

    public List<NotesDto> getAllNotes() {
        List<Notes> notes = notesRepository.findAll();
        return notes.stream().map(this::mapToNotesDto).collect(Collectors.toList());
    }

    public NotesDto getNoteById(Long noteId) {
        Notes note = notesRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + noteId));
        return mapToNotesDto(note);
    }

    public List<NotesDto> getNotesByCourse(Long courseId) {
        List<Notes> notes = notesRepository.findByCourseId(courseId);
        return notes.stream().map(this::mapToNotesDto).collect(Collectors.toList());
    }

    public List<NotesDto> getNotesByUploader(Long userId) {
        List<Notes> notes = notesRepository.findByUploadedById(userId);
        return notes.stream().map(this::mapToNotesDto).collect(Collectors.toList());
    }

    public List<NotesDto> getNotesByCourseAndUploader(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        User uploadedBy = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Notes> notes = notesRepository.findByCourseAndUploadedBy(course, uploadedBy);
        return notes.stream().map(this::mapToNotesDto).collect(Collectors.toList());
    }

    public NotesDto updateNote(Long noteId, NotesRequest notesRequest, UserDetailsImpl currentUser) {
        try {
            Notes note = notesRepository.findById(noteId)
                    .orElseThrow(() -> new RuntimeException("Note not found"));

            // Check if current user is the note uploader, course instructor, or admin
            boolean isAdmin = currentUser.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            boolean isNoteUploader = note.getUploadedBy().getUserId().equals(currentUser.getUserId());
            boolean isCourseInstructor = note.getCourse().getInstructor().getUserId().equals(currentUser.getUserId());

            if (!isAdmin && !isNoteUploader && !isCourseInstructor) {
                throw new AccessDeniedException("You are not authorized to update this note");
            }

            Course course = courseRepository.findById(notesRequest.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            // If new file is provided, store it and update fileUrl
            if (notesRequest.getFile() != null && !notesRequest.getFile().isEmpty()) {
                String storedFilename = fileStorageService.storeFile(notesRequest.getFile());
                String fileUrl = fileStorageService.getFileUrl(storedFilename);
                note.setFileUrl(fileUrl);
            }

            note.setTitle(notesRequest.getTitle());
            note.setCourse(course);

            Notes updatedNote = notesRepository.save(note);
            return mapToNotesDto(updatedNote);

        } catch (Exception e) {
            throw new RuntimeException("Failed to update note: " + e.getMessage());
        }
    }

    public void deleteNote(Long noteId, UserDetailsImpl currentUser) {
        Notes note = notesRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        // Check if current user is the note uploader, course instructor, or admin
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isNoteUploader = note.getUploadedBy().getUserId().equals(currentUser.getUserId());
        boolean isCourseInstructor = note.getCourse().getInstructor().getUserId().equals(currentUser.getUserId());

        if (!isAdmin && !isNoteUploader && !isCourseInstructor) {
            throw new AccessDeniedException("You are not authorized to delete this note");
        }

        // Delete the physical file first
        try {
            String filename = note.getFileUrl().replace("/api/files/", "");
            fileStorageService.deleteFile(filename);
        } catch (Exception e) {
            System.err.println("Failed to delete physical file: " + e.getMessage());
        }

        // Delete the database record
        notesRepository.delete(note);
    }

    private NotesDto mapToNotesDto(Notes note) {
        return NotesDto.builder()
                .noteId(note.getNoteId())
                .title(note.getTitle())
                .fileUrl(note.getFileUrl())
                .createdAt(note.getCreatedAt())
                .courseId(note.getCourse().getCourseId())
                .courseTitle(note.getCourse().getTitle())
                .uploadedById(note.getUploadedBy().getUserId())
                .uploadedByName(note.getUploadedBy().getName())
                .build();
    }
}