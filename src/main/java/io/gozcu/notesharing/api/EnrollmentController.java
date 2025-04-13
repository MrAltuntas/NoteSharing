package io.gozcu.notesharing.api;

import io.gozcu.notesharing.model.EnrollmentRequest;
import io.gozcu.notesharing.model.EnrollmentResponse;
import io.gozcu.notesharing.model.EnrollmentsResponse;
import io.gozcu.notesharing.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnrollmentController implements EnrollmentsApi {

    @Autowired
    private EnrollmentService enrollmentService;

    @Override
    public ResponseEntity<EnrollmentResponse> enrollStudentInCourse(Long courseId, EnrollmentRequest enrollmentRequest) {
        EnrollmentResponse response = enrollmentService.enrollStudentInCourse(courseId, enrollmentRequest);

        if (!response.getSuccess()) {
            if (response.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else if (response.getMessage().contains("already enrolled")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<EnrollmentsResponse> getEnrolledStudents(Long courseId, Integer page, Integer size) {
        EnrollmentsResponse response = enrollmentService.getEnrolledStudents(courseId, page, size);

        if (!response.getSuccess()) {
            if (response.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.ok(response);
    }
}