package io.gozcu.notesharing.service;

import java.util.Map;

public interface CourseService {
    Map<String, Object> createCourse();
    Map<String, Object> getAllCourses();
    Map<String, Object> getCourseById(Long courseId);
    Map<String, Object> updateCourse(Long courseId);
    Map<String, Object> deleteCourse(Long courseId);
    Map<String, Object> rateCourse(Long courseId);
    Map<String, Object> searchCourses(String query);
    Map<String, Object> visitCourse(Long courseId);
}