package io.gozcu.notesharing.api;

import io.gozcu.notesharing.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCourse() {
        Map<String, Object> response = courseService.createCourse();
        boolean success = (boolean) response.getOrDefault("success", false);
        return success
                ? new ResponseEntity<>(response, HttpStatus.CREATED)
                : new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCourses() {
        Map<String, Object> response = courseService.getAllCourses();
        boolean success = (boolean) response.getOrDefault("success", false);
        return success
                ? ResponseEntity.ok(response)
                : new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Map<String, Object>> getCourseById(@PathVariable Long courseId) {
        Map<String, Object> response = courseService.getCourseById(courseId);
        boolean success = (boolean) response.getOrDefault("success", false);
        return success
                ? ResponseEntity.ok(response)
                : new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Map<String, Object>> updateCourse(@PathVariable Long courseId) {
        Map<String, Object> response = courseService.updateCourse(courseId);
        boolean success = (boolean) response.getOrDefault("success", false);
        return success
                ? ResponseEntity.ok(response)
                : new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable Long courseId) {
        Map<String, Object> response = courseService.deleteCourse(courseId);
        boolean success = (boolean) response.getOrDefault("success", false);
        return success
                ? ResponseEntity.ok(response)
                : new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{courseId}/rate")
    public ResponseEntity<Map<String, Object>> rateCourse(@PathVariable Long courseId) {
        Map<String, Object> response = courseService.rateCourse(courseId);
        boolean success = (boolean) response.getOrDefault("success", false);
        return success
                ? ResponseEntity.ok(response)
                : new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCourses(@RequestParam String query) {
        Map<String, Object> response = courseService.searchCourses(query);
        boolean success = (boolean) response.getOrDefault("success", false);
        return success
                ? ResponseEntity.ok(response)
                : new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/{courseId}/visit")
    public ResponseEntity<Map<String, Object>> visitCourse(@PathVariable Long courseId) {
        Map<String, Object> response = courseService.visitCourse(courseId);
        boolean success = (boolean) response.getOrDefault("success", false);
        return success
                ? ResponseEntity.ok(response)
                : new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}