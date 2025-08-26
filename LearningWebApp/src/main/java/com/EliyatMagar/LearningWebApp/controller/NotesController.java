package com.EliyatMagar.LearningWebApp.controller;

import com.EliyatMagar.LearningWebApp.dto.NotesDto;
import com.EliyatMagar.LearningWebApp.dto.request.NotesRequest;
import com.EliyatMagar.LearningWebApp.security.services.UserDetailsImpl;
import com.EliyatMagar.LearningWebApp.service.NotesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NotesController {

    private final NotesService notesService;

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<NotesDto> createNote(
            @ModelAttribute NotesRequest notesRequest,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        NotesDto createdNote = notesService.createNote(notesRequest, currentUser);
        return new ResponseEntity<>(createdNote, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<NotesDto>> getAllNotes() {
        List<NotesDto> notes = notesService.getAllNotes();
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NotesDto> getNoteById(@PathVariable Long noteId) {
        NotesDto note = notesService.getNoteById(noteId);
        return ResponseEntity.ok(note);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<NotesDto>> getNotesByCourse(@PathVariable Long courseId) {
        List<NotesDto> notes = notesService.getNotesByCourse(courseId);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotesDto>> getNotesByUploader(@PathVariable Long userId) {
        List<NotesDto> notes = notesService.getNotesByUploader(userId);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/course/{courseId}/user/{userId}")
    public ResponseEntity<List<NotesDto>> getNotesByCourseAndUploader(
            @PathVariable Long courseId,
            @PathVariable Long userId) {
        List<NotesDto> notes = notesService.getNotesByCourseAndUploader(courseId, userId);
        return ResponseEntity.ok(notes);
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<NotesDto> updateNote(
            @PathVariable Long noteId,
            @ModelAttribute NotesRequest notesRequest,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        NotesDto updatedNote = notesService.updateNote(noteId, notesRequest, currentUser);
        return ResponseEntity.ok(updatedNote);
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long noteId,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        notesService.deleteNote(noteId, currentUser);
        return ResponseEntity.noContent().build();
    }
}