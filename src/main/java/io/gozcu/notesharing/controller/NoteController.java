package io.gozcu.notesharing.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/notes")
public class NoteController {

    @PostMapping("/courses/{courseId}")
    public ResponseEntity<?> addNote(@PathVariable Long courseId) {
        // Note addition process will be implemented here
        Map<String, Object> note = createDummyNote(201L, courseId);

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Note added successfully",
                "note", note
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<?> getNotesByCourseId(@PathVariable Long courseId) {
        // Process to get notes by course ID will be implemented here
        List<Map<String, Object>> notes = new ArrayList<>();

        notes.add(createDummyNote(201L, courseId));
        notes.add(createDummyNote(202L, courseId));
        notes.add(createDummyNote(203L, courseId));

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Notes retrieved successfully",
                "courseId", courseId,
                "totalNotes", notes.size(),
                "notes", notes
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<?> getNoteById(@PathVariable Long noteId) {
        // Process to get a note by ID will be implemented here
        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Note information retrieved successfully",
                "note", createDummyNote(noteId, 101L)
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<?> updateNote(@PathVariable Long noteId) {
        // Note update process will be implemented here
        Map<String, Object> updatedNote = new HashMap<>(createDummyNote(noteId, 101L));
        ((HashMap<String, Object>)updatedNote).put("title", "Updated Note Title");
        ((HashMap<String, Object>)updatedNote).put("content", "This note shows updated content.");

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Note updated successfully",
                "note", updatedNote
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<?> deleteNote(@PathVariable Long noteId) {
        // Note deletion process will be implemented here
        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Note deleted successfully",
                "noteId", noteId
        );

        return ResponseEntity.ok(response);
    }

    // Helper method - creates a dummy note
    private Map<String, Object> createDummyNote(Long id, Long courseId) {
        List<Map<String, Object>> attachments = Arrays.asList(
                Map.of("id", 301, "name", "document.pdf", "size", 1024 * 1024),
                Map.of("id", 302, "name", "image.jpg", "size", 512 * 1024)
        );

        return Map.of(
                "id", id,
                "courseId", courseId,
                "title", "Sample Note " + id,
                "content", "This is a sample note content. This note contains important information about Spring Boot.",
                "author", "User" + new Random().nextInt(1000),
                "createdAt", LocalDateTime.now().toString(),
                "updatedAt", LocalDateTime.now().toString(),
                "likes", new Random().nextInt(50),
                "attachments", attachments
        );
    }
}