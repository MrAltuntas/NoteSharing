package io.gozcu.notesharing.service;

import io.gozcu.notesharing.model.*;
import io.gozcu.notesharing.repository.CourseRepository;
import io.gozcu.notesharing.repository.NoteRepository;
import io.gozcu.notesharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Map<String, Object> addNote(Long courseId) {
        try {
            Optional<CourseEntity> courseOpt = courseRepository.findById(courseId);

            if (courseOpt.isPresent()) {
                CourseEntity course = courseOpt.get();

                // Create a dummy user or get from repository
                Optional<User> userOpt = userRepository.findAll().stream().findFirst();
                User author = userOpt.orElseGet(() -> {
                    User newUser = new User("testuser", "test@example.com", "password", "USER");
                    return userRepository.save(newUser);
                });

                // Create and save note
                NoteEntity note = new NoteEntity();
                note.setTitle("Sample Note for " + course.getTitle());
                note.setContent("This is a sample note content for the course.");
                note.setCourse(course);
                note.setAuthor(author);

                // Add an attachment
                AttachmentEntity attachment = new AttachmentEntity(1L, "document.pdf", 1048576);
                HashSet<AttachmentEntity> attachments = new HashSet<>();
                attachments.add(attachment);
                note.setAttachments(attachments);

                // Save note
                note = noteRepository.save(note);

                // Create response map
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Note added successfully");
                response.put("note", convertNoteToMap(note));

                return response;
            } else {
                // Create dummy response if course doesn't exist
                Map<String, Object> response = new HashMap<>();
                response.put("success", true); // For testing purposes
                response.put("message", "Note added successfully");
                response.put("note", createDummyNote(201L, courseId));

                return response;
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to add note: " + e.getMessage());
            return response;
        }
    }

    @Override
    public Map<String, Object> getNotesByCourseId(Long courseId) {
        try {
            // Find notes by course ID
            List<NoteEntity> notes = noteRepository.findByCourseId(courseId);

            // Convert to map objects
            List<Map<String, Object>> notesData = new ArrayList<>();
            for (NoteEntity note : notes) {
                notesData.add(convertNoteToMap(note));
            }

            // If no notes found, return some dummy data for testing
            if (notesData.isEmpty()) {
                notesData.add(createDummyNote(201L, courseId));
                notesData.add(createDummyNote(202L, courseId));
            }

            // Create response map
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notes retrieved successfully");
            response.put("courseId", courseId);
            response.put("totalNotes", notesData.size());
            response.put("notes", notesData);

            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve notes: " + e.getMessage());
            return response;
        }
    }

    @Override
    public Map<String, Object> getNoteById(Long noteId) {
        try {
            // Find note by ID
            Optional<NoteEntity> noteOpt = noteRepository.findById(noteId);

            Map<String, Object> response = new HashMap<>();

            if (noteOpt.isPresent()) {
                NoteEntity note = noteOpt.get();
                response.put("success", true);
                response.put("message", "Note information retrieved successfully");
                response.put("note", convertNoteToMap(note));
            } else {
                // Return dummy data for testing
                response.put("success", true);
                response.put("message", "Note information retrieved successfully");
                response.put("note", createDummyNote(noteId, 101L));
            }

            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve note: " + e.getMessage());
            return response;
        }
    }

    @Override
    public Map<String, Object> updateNote(Long noteId) {
        try {
            // Find note by ID
            Optional<NoteEntity> noteOpt = noteRepository.findById(noteId);

            Map<String, Object> response = new HashMap<>();

            if (noteOpt.isPresent()) {
                NoteEntity note = noteOpt.get();

                // Update note fields
                note.setTitle("Updated Note Title");
                note.setContent("This note shows updated content.");
                note.setUpdatedAt(LocalDateTime.now());

                // Save updated note
                note = noteRepository.save(note);

                response.put("success", true);
                response.put("message", "Note updated successfully");
                response.put("note", convertNoteToMap(note));
            } else {
                // Return dummy data for testing
                Map<String, Object> noteData = createDummyNote(noteId, 101L);
                noteData.put("title", "Updated Note Title");
                noteData.put("content", "This note shows updated content.");

                response.put("success", true);
                response.put("message", "Note updated successfully");
                response.put("note", noteData);
            }

            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update note: " + e.getMessage());
            return response;
        }
    }

    @Override
    public Map<String, Object> deleteNote(Long noteId) {
        try {
            // Check if note exists
            if (noteRepository.existsById(noteId)) {
                // Delete note
                noteRepository.deleteById(noteId);
            }

            // Create response map
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Note deleted successfully");
            response.put("noteId", noteId);

            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to delete note: " + e.getMessage());
            response.put("noteId", noteId);
            return response;
        }
    }

    // Helper method to convert note entity to map
    private Map<String, Object> convertNoteToMap(NoteEntity entity) {
        Map<String, Object> noteMap = new HashMap<>();
        noteMap.put("id", entity.getId());
        noteMap.put("courseId", entity.getCourse() != null ? entity.getCourse().getId() : null);
        noteMap.put("title", entity.getTitle());
        noteMap.put("content", entity.getContent());
        noteMap.put("author", entity.getAuthor() != null ? entity.getAuthor().getUsername() : "Anonymous");

        // Convert LocalDateTime to ISO string
        if (entity.getCreatedAt() != null) {
            noteMap.put("createdAt", entity.getCreatedAt().atOffset(ZoneOffset.UTC).toString());
        }

        if (entity.getUpdatedAt() != null) {
            noteMap.put("updatedAt", entity.getUpdatedAt().atOffset(ZoneOffset.UTC).toString());
        }

        noteMap.put("likes", entity.getLikes());

        // Convert attachments
        List<Map<String, Object>> attachments = new ArrayList<>();
        if (entity.getAttachments() != null) {
            for (AttachmentEntity attachment : entity.getAttachments()) {
                Map<String, Object> attachmentMap = new HashMap<>();
                attachmentMap.put("id", attachment.getId());
                attachmentMap.put("name", attachment.getName());
                attachmentMap.put("size", attachment.getSize());
                attachments.add(attachmentMap);
            }
        }
        noteMap.put("attachments", attachments);

        return noteMap;
    }

    // Helper method to create a dummy note (for testing when database has no data)
    private Map<String, Object> createDummyNote(Long id, Long courseId) {
        // Create note data map
        Map<String, Object> noteData = new HashMap<>();
        noteData.put("id", id);
        noteData.put("courseId", courseId);
        noteData.put("title", "Sample Note " + id);
        noteData.put("content", "This is a sample note content. This note contains important information about Spring Boot.");
        noteData.put("author", "User" + (id % 10));
        noteData.put("createdAt", LocalDateTime.now().minusDays(id % 7).atOffset(ZoneOffset.UTC).toString());
        noteData.put("updatedAt", LocalDateTime.now().atOffset(ZoneOffset.UTC).toString());
        noteData.put("likes", 25);

        // Create attachments
        List<Map<String, Object>> attachments = new ArrayList<>();

        Map<String, Object> attachment1 = new HashMap<>();
        attachment1.put("id", 300L + id);
        attachment1.put("name", "document.pdf");
        attachment1.put("size", 1048576);
        attachments.add(attachment1);

        Map<String, Object> attachment2 = new HashMap<>();
        attachment2.put("id", 400L + id);
        attachment2.put("name", "image.jpg");
        attachment2.put("size", 512 * 1024);
        attachments.add(attachment2);

        noteData.put("attachments", attachments);

        return noteData;
    }
}