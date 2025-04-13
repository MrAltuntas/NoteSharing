package io.gozcu.notesharing.service;

import java.util.Map;

public interface NoteService {
    Map<String, Object> addNote(Long courseId);
    Map<String, Object> getNotesByCourseId(Long courseId);
    Map<String, Object> getNoteById(Long noteId);
    Map<String, Object> updateNote(Long noteId);
    Map<String, Object> deleteNote(Long noteId);
}