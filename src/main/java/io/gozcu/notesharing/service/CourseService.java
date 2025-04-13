package io.gozcu.notesharing.service;

import io.gozcu.notesharing.model.CourseListResponse;
import io.gozcu.notesharing.model.CourseRequest;
import io.gozcu.notesharing.model.CourseResponse;
import io.gozcu.notesharing.model.DeleteResponse;
import io.gozcu.notesharing.model.RatingRequest;
import io.gozcu.notesharing.model.RatingResponse;
import io.gozcu.notesharing.model.SearchResponse;
import io.gozcu.notesharing.model.VisitResponse;

public interface CourseService {
    CourseResponse createCourse(CourseRequest courseRequest);
    CourseListResponse getAllCourses(Integer page, Integer size, String sort);
    CourseResponse getCourseById(Long courseId);
    CourseResponse updateCourse(Long courseId, CourseRequest courseRequest);
    DeleteResponse deleteCourse(Long courseId);
    RatingResponse rateCourse(Long courseId, RatingRequest ratingRequest);
    SearchResponse searchCourses(String query);
    VisitResponse visitCourse(Long courseId);
}