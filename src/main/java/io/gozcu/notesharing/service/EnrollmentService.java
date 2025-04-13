package io.gozcu.notesharing.service;

import io.gozcu.notesharing.model.EnrollmentRequest;
import io.gozcu.notesharing.model.EnrollmentResponse;
import io.gozcu.notesharing.model.EnrollmentsResponse;

public interface EnrollmentService {
    EnrollmentResponse enrollStudentInCourse(Long courseId, EnrollmentRequest enrollmentRequest);
    EnrollmentsResponse getEnrolledStudents(Long courseId, Integer page, Integer size);
}