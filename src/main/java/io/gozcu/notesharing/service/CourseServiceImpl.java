package io.gozcu.notesharing.service;

import io.gozcu.notesharing.model.CourseEntity;
import io.gozcu.notesharing.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public Map<String, Object> createCourse() {
        try {
            // Create a new course entity
            CourseEntity courseEntity = new CourseEntity();
            courseEntity.setTitle("Sample Course");
            courseEntity.setDescription("This is a sample course description");
            courseEntity.setInstructor("Prof. John Doe");

            // Save entity
            courseEntity = courseRepository.save(courseEntity);

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Course created successfully");
            response.put("course", convertEntityToMap(courseEntity));

            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to create course: " + e.getMessage());
            return response;
        }
    }

    @Override
    public Map<String, Object> getAllCourses() {
        try {
            // Get all courses from repository
            List<CourseEntity> courseEntities = courseRepository.findAll();

            // Convert entities to map objects
            List<Map<String, Object>> courses = courseEntities.stream()
                    .map(this::convertEntityToMap)
                    .collect(Collectors.toList());

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Courses retrieved successfully");
            response.put("totalCourses", courses.size());
            response.put("courses", courses);

            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving courses: " + e.getMessage());
            return response;
        }
    }

    @Override
    public Map<String, Object> getCourseById(Long courseId) {
        try {
            // Find course by ID
            Optional<CourseEntity> courseOpt = courseRepository.findById(courseId);

            Map<String, Object> response = new HashMap<>();

            if (courseOpt.isPresent()) {
                CourseEntity courseEntity = courseOpt.get();
                response.put("success", true);
                response.put("message", "Course retrieved successfully");
                response.put("course", convertEntityToMap(courseEntity));
            } else {
                response.put("success", false);
                response.put("message", "Course not found with ID: " + courseId);
            }

            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving course: " + e.getMessage());
            return response;
        }
    }

    @Override
    public Map<String, Object> updateCourse(Long courseId) {
        try {
            // Find course by ID
            Optional<CourseEntity> courseOpt = courseRepository.findById(courseId);

            Map<String, Object> response = new HashMap<>();

            if (courseOpt.isPresent()) {
                CourseEntity courseEntity = courseOpt.get();

                // Update course fields
                courseEntity.setTitle("Updated Course Title");
                courseEntity.setDescription("This is an updated course description");
                courseEntity.setInstructor("Prof. Jane Smith");
                courseEntity.setUpdatedAt(LocalDateTime.now());

                // Save updated entity
                courseEntity = courseRepository.save(courseEntity);

                response.put("success", true);
                response.put("message", "Course updated successfully");
                response.put("course", convertEntityToMap(courseEntity));
            } else {
                response.put("success", false);
                response.put("message", "Course not found with ID: " + courseId);
            }

            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error updating course: " + e.getMessage());
            return response;
        }
    }

    @Override
    public Map<String, Object> deleteCourse(Long courseId) {
        try {
            // Check if course exists
            if (courseRepository.existsById(courseId)) {
                // Delete course
                courseRepository.deleteById(courseId);

                // Create response
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Course deleted successfully");
                response.put("courseId", courseId);

                return response;
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Course not found with ID: " + courseId);
                response.put("courseId", courseId);

                return response;
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error deleting course: " + e.getMessage());
            response.put("courseId", courseId);

            return response;
        }
    }

    @Override
    public Map<String, Object> rateCourse(Long courseId) {
        try {
            // Find course by ID
            Optional<CourseEntity> courseOpt = courseRepository.findById(courseId);

            if (courseOpt.isPresent()) {
                CourseEntity courseEntity = courseOpt.get();

                // Simulate rating
                double rating = 4.5;
                double currentRating = courseEntity.getRating() != null ? courseEntity.getRating() : 0.0;
                int totalRatings = courseEntity.getTotalRatings() != null ? courseEntity.getTotalRatings() : 0;

                // Calculate new rating
                double newRating = (currentRating * totalRatings + rating) / (totalRatings + 1);
                courseEntity.setRating(newRating);
                courseEntity.setTotalRatings(totalRatings + 1);

                // Save updated entity
                courseRepository.save(courseEntity);

                // Create response
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Course rated successfully");
                response.put("courseId", courseId);
                response.put("rating", newRating);
                response.put("totalRatings", totalRatings + 1);

                return response;
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Course not found with ID: " + courseId);
                response.put("courseId", courseId);

                return response;
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error rating course: " + e.getMessage());
            response.put("courseId", courseId);

            return response;
        }
    }

    @Override
    public Map<String, Object> searchCourses(String query) {
        try {
            // Search for courses
            List<CourseEntity> courseEntities = courseRepository.search(query);

            // Convert entities to map objects
            List<Map<String, Object>> courses = courseEntities.stream()
                    .map(this::convertEntityToMap)
                    .collect(Collectors.toList());

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Search results retrieved successfully");
            response.put("query", query);
            response.put("totalResults", courses.size());
            response.put("results", courses);

            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error searching courses: " + e.getMessage());
            response.put("query", query);

            return response;
        }
    }

    @Override
    public Map<String, Object> visitCourse(Long courseId) {
        try {
            // Find course by ID
            Optional<CourseEntity> courseOpt = courseRepository.findById(courseId);

            if (courseOpt.isPresent()) {
                CourseEntity courseEntity = courseOpt.get();

                // Update visit count
                int visits = courseEntity.getTotalVisits() != null ? courseEntity.getTotalVisits() : 0;
                courseEntity.setTotalVisits(visits + 1);

                // Save updated entity
                courseRepository.save(courseEntity);

                // Create response
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Course visit recorded");
                response.put("courseId", courseId);
                response.put("visitCount", visits + 1);

                return response;
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Course not found with ID: " + courseId);
                response.put("courseId", courseId);

                return response;
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error recording course visit: " + e.getMessage());
            response.put("courseId", courseId);

            return response;
        }
    }

    // Helper method to convert entity to map
    private Map<String, Object> convertEntityToMap(CourseEntity entity) {
        Map<String, Object> courseMap = new HashMap<>();
        courseMap.put("id", entity.getId());
        courseMap.put("title", entity.getTitle());
        courseMap.put("description", entity.getDescription());
        courseMap.put("instructor", entity.getInstructor());

        // Convert LocalDateTime to ISO string
        if (entity.getCreatedAt() != null) {
            courseMap.put("createdAt", entity.getCreatedAt().atOffset(ZoneOffset.UTC).toString());
        }

        if (entity.getUpdatedAt() != null) {
            courseMap.put("updatedAt", entity.getUpdatedAt().atOffset(ZoneOffset.UTC).toString());
        }

        courseMap.put("rating", entity.getRating());
        courseMap.put("totalRatings", entity.getTotalRatings());
        courseMap.put("totalVisits", entity.getTotalVisits());

        // Convert tags
        if (entity.getTags() != null) {
            courseMap.put("tags", new ArrayList<>(entity.getTags()));
        } else {
            courseMap.put("tags", new ArrayList<>());
        }

        return courseMap;
    }
}