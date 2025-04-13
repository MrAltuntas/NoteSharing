package io.gozcu.notesharing.api;

import io.gozcu.notesharing.model.CourseListResponse;
import io.gozcu.notesharing.model.CourseRequest;
import io.gozcu.notesharing.model.CourseResponse;
import io.gozcu.notesharing.model.DeleteResponse;
import io.gozcu.notesharing.model.RatingRequest;
import io.gozcu.notesharing.model.RatingResponse;
import io.gozcu.notesharing.model.SearchResponse;
import io.gozcu.notesharing.model.VisitResponse;
import io.gozcu.notesharing.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseController implements CoursesApi {

    @Autowired
    private CourseService courseService;

    @Override
    public ResponseEntity<CourseResponse> createCourse(CourseRequest courseRequest) {
        CourseResponse response = courseService.createCourse(courseRequest);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<CourseListResponse> getAllCourses(Integer page, Integer size, String sort) {
        CourseListResponse response = courseService.getAllCourses(page, size, sort);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CourseResponse> getCourseById(Long courseId) {
        CourseResponse response = courseService.getCourseById(courseId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CourseResponse> updateCourse(Long courseId, CourseRequest courseRequest) {
        CourseResponse response = courseService.updateCourse(courseId, courseRequest);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<DeleteResponse> deleteCourse(Long courseId) {
        DeleteResponse response = courseService.deleteCourse(courseId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<RatingResponse> rateCourse(Long courseId, RatingRequest ratingRequest) {
        RatingResponse response = courseService.rateCourse(courseId, ratingRequest);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SearchResponse> searchCourses(String query) {
        SearchResponse response = courseService.searchCourses(query);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<VisitResponse> visitCourse(Long courseId) {
        VisitResponse response = courseService.visitCourse(courseId);
        return ResponseEntity.ok(response);
    }
}