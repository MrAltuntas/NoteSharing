package io.gozcu.notesharing.api;

import io.gozcu.notesharing.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @PostMapping("/courses/{courseId}")
    public ResponseEntity<Map<String, Object>> addNote(@PathVariable Long courseId) {
        Map<String, Object> response = noteService.addNote(courseId);
        boolean success = (boolean) response.getOrDefault("success", false);
        return success
                ? new ResponseEntity<>(response, HttpStatus.CREATED)
                : new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<Map<String, Object>> getNotesByCourseId(@PathVariable Long courseId) {
        Map<String, Object> response = noteService.getNotesByCourseId(courseId);
        boolean success = (boolean) response.getOrDefault("success", false);
        return success
                ? ResponseEntity.ok(response)
                : new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<Map<String, Object>> getNoteById(@PathVariable Long noteId) {
        Map<String, Object> response = noteService.getNoteById(noteId);
        boolean success = (boolean) response.getOrDefault("success", false);
        return success
                ? ResponseEntity.ok(response)
                : new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<Map<String, Object>> updateNote(@PathVariable Long noteId) {
        Map<String, Object> response = noteService.updateNote(noteId);
        boolean success = (boolean) response.getOrDefault("success", false);
        return success
                ? ResponseEntity.ok(response)
                : new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Map<String, Object>> deleteNote(@PathVariable Long noteId) {
        Map<String, Object> response = noteService.deleteNote(noteId);
        boolean success = (boolean) response.getOrDefault("success", false);
        return success
                ? ResponseEntity.ok(response)
                : new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}