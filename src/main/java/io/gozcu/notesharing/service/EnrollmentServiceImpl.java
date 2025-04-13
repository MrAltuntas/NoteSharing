package io.gozcu.notesharing.service;

import io.gozcu.notesharing.model.*;
import io.gozcu.notesharing.repository.CourseRepository;
import io.gozcu.notesharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public EnrollmentResponse enrollStudentInCourse(Long courseId, EnrollmentRequest enrollmentRequest) {
        try {
            // Find course by ID
            Optional<CourseEntity> courseOpt = courseRepository.findById(courseId);
            if (!courseOpt.isPresent()) {
                EnrollmentResponse response = new EnrollmentResponse();
                response.setSuccess(false);
                response.setMessage("Course not found with ID: " + courseId);
                return response;
            }

            // Find student by ID
            Optional<User> studentOpt = userRepository.findById(enrollmentRequest.getStudentId());
            if (!studentOpt.isPresent()) {
                EnrollmentResponse response = new EnrollmentResponse();
                response.setSuccess(false);
                response.setMessage("Student not found with ID: " + enrollmentRequest.getStudentId());
                return response;
            }

            CourseEntity course = courseOpt.get();
            User student = studentOpt.get();

            // Check if student is already enrolled in the course
            if (student.getEnrolledCourses().contains(course)) {
                EnrollmentResponse response = new EnrollmentResponse();
                response.setSuccess(false);
                response.setMessage("Student is already enrolled in this course");
                response.setCourseId(courseId);
                response.setStudentId(student.getId());
                return response;
            }

            // Enroll student in course using the helper method
            student.enrollInCourse(course);

            // Save the changes (this will update both sides of the relationship)
            userRepository.save(student);

            // Create response
            EnrollmentResponse response = new EnrollmentResponse();
            response.setSuccess(true);
            response.setMessage("Student enrolled successfully");
            response.setCourseId(courseId);
            response.setStudentId(student.getId());
            response.setEnrollmentDate(LocalDateTime.now().atOffset(ZoneOffset.UTC));

            return response;
        } catch (Exception e) {
            EnrollmentResponse response = new EnrollmentResponse();
            response.setSuccess(false);
            response.setMessage("Error enrolling student: " + e.getMessage());
            return response;
        }
    }

    @Override
    public EnrollmentsResponse getEnrolledStudents(Long courseId, Integer page, Integer size) {
        try {
            // Find course by ID
            Optional<CourseEntity> courseOpt = courseRepository.findById(courseId);
            if (!courseOpt.isPresent()) {
                EnrollmentsResponse response = new EnrollmentsResponse();
                response.setSuccess(false);
                response.setMessage("Course not found with ID: " + courseId);
                return response;
            }

            CourseEntity course = courseOpt.get();

            // Get all enrolled students
            List<User> allEnrolledStudents = new ArrayList<>(course.getEnrolledStudents());

            // Calculate total number of students
            int totalStudents = allEnrolledStudents.size();

            // Apply pagination manually
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, totalStudents);

            List<User> paginatedStudents = new ArrayList<>();
            if (startIndex < totalStudents) {
                paginatedStudents = allEnrolledStudents.subList(startIndex, endIndex);
            }

            // Convert students to DTOs
            List<UserDTO> studentDTOs = paginatedStudents.stream()
                    .map(this::convertUserToDTO)
                    .collect(Collectors.toList());

            // Create response
            EnrollmentsResponse response = new EnrollmentsResponse();
            response.setSuccess(true);
            response.setMessage("Enrolled students retrieved successfully");
            response.setCourseId(courseId);
            response.setTotalStudents(totalStudents);
            response.setStudents(studentDTOs);

            return response;
        } catch (Exception e) {
            EnrollmentsResponse response = new EnrollmentsResponse();
            response.setSuccess(false);
            response.setMessage("Error retrieving enrolled students: " + e.getMessage());
            return response;
        }
    }

    // Helper method to convert User to UserDTO
    private UserDTO convertUserToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId().intValue());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        return userDTO;
    }
}