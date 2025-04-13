package io.gozcu.notesharing.service;

import io.gozcu.notesharing.model.CourseDTO;
import io.gozcu.notesharing.model.CourseEntity;
import io.gozcu.notesharing.model.CourseListResponse;
import io.gozcu.notesharing.model.CourseRequest;
import io.gozcu.notesharing.model.CourseResponse;
import io.gozcu.notesharing.model.DeleteResponse;
import io.gozcu.notesharing.model.RatingRequest;
import io.gozcu.notesharing.model.RatingResponse;
import io.gozcu.notesharing.model.SearchResponse;
import io.gozcu.notesharing.model.VisitResponse;
import io.gozcu.notesharing.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public CourseResponse createCourse(CourseRequest courseRequest) {
        try {
            // Create a new course entity from request
            CourseEntity courseEntity = new CourseEntity();
            courseEntity.setTitle(courseRequest.getTitle());
            courseEntity.setDescription(courseRequest.getDescription());
            courseEntity.setInstructor(courseRequest.getInstructor());

            // Set tags if provided
            if (courseRequest.getTags() != null && !courseRequest.getTags().isEmpty()) {
                courseEntity.setTags(new HashSet<>(courseRequest.getTags()));
            }

            // Save entity
            courseEntity = courseRepository.save(courseEntity);

            // Create response
            CourseResponse response = new CourseResponse();
            response.setSuccess(true);
            response.setMessage("Course created successfully");
            response.setCourse(convertEntityToDTO(courseEntity));

            return response;
        } catch (Exception e) {
            CourseResponse response = new CourseResponse();
            response.setSuccess(false);
            response.setMessage("Failed to create course: " + e.getMessage());
            return response;
        }
    }

    @Override
    public CourseListResponse getAllCourses(Integer page, Integer size, String sort) {
        try {
            // Create pageable request
            Pageable pageable;
            if (sort != null && !sort.isEmpty()) {
                String[] sortParams = sort.split(",");
                String sortField = sortParams[0];
                Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc") ?
                        Sort.Direction.DESC : Sort.Direction.ASC;
                pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
            } else {
                pageable = PageRequest.of(page, size);
            }

            // Get all courses from repository with pagination
            Page<CourseEntity> coursesPage = courseRepository.findAll(pageable);
            List<CourseEntity> courseEntities = coursesPage.getContent();

            // Convert entities to DTOs
            List<CourseDTO> courseDTOs = courseEntities.stream()
                    .map(this::convertEntityToDTO)
                    .collect(Collectors.toList());

            // Create response
            CourseListResponse response = new CourseListResponse();
            response.setSuccess(true);
            response.setMessage("Courses retrieved successfully");
            response.setTotalCourses((int) coursesPage.getTotalElements());
            response.setCourses(courseDTOs);

            return response;
        } catch (Exception e) {
            CourseListResponse response = new CourseListResponse();
            response.setSuccess(false);
            response.setMessage("Error retrieving courses: " + e.getMessage());
            return response;
        }
    }

    @Override
    public CourseResponse getCourseById(Long courseId) {
        try {
            // Find course by ID
            Optional<CourseEntity> courseOpt = courseRepository.findById(courseId);

            CourseResponse response = new CourseResponse();

            if (courseOpt.isPresent()) {
                CourseEntity courseEntity = courseOpt.get();
                response.setSuccess(true);
                response.setMessage("Course retrieved successfully");
                response.setCourse(convertEntityToDTO(courseEntity));
            } else {
                response.setSuccess(false);
                response.setMessage("Course not found with ID: " + courseId);
            }

            return response;
        } catch (Exception e) {
            CourseResponse response = new CourseResponse();
            response.setSuccess(false);
            response.setMessage("Error retrieving course: " + e.getMessage());
            return response;
        }
    }

    @Override
    public CourseResponse updateCourse(Long courseId, CourseRequest courseRequest) {
        try {
            // Find course by ID
            Optional<CourseEntity> courseOpt = courseRepository.findById(courseId);

            CourseResponse response = new CourseResponse();

            if (courseOpt.isPresent()) {
                CourseEntity courseEntity = courseOpt.get();

                // Update course fields
                courseEntity.setTitle(courseRequest.getTitle());
                if (courseRequest.getDescription() != null) {
                    courseEntity.setDescription(courseRequest.getDescription());
                }
                if (courseRequest.getInstructor() != null) {
                    courseEntity.setInstructor(courseRequest.getInstructor());
                }
                if (courseRequest.getTags() != null) {
                    courseEntity.setTags(new HashSet<>(courseRequest.getTags()));
                }

                courseEntity.setUpdatedAt(LocalDateTime.now());

                // Save updated entity
                courseEntity = courseRepository.save(courseEntity);

                response.setSuccess(true);
                response.setMessage("Course updated successfully");
                response.setCourse(convertEntityToDTO(courseEntity));
            } else {
                response.setSuccess(false);
                response.setMessage("Course not found with ID: " + courseId);
            }

            return response;
        } catch (Exception e) {
            CourseResponse response = new CourseResponse();
            response.setSuccess(false);
            response.setMessage("Error updating course: " + e.getMessage());
            return response;
        }
    }

    @Override
    public DeleteResponse deleteCourse(Long courseId) {
        try {
            // Check if course exists
            if (courseRepository.existsById(courseId)) {
                // Delete course
                courseRepository.deleteById(courseId);

                // Create response
                DeleteResponse response = new DeleteResponse();
                response.setSuccess(true);
                response.setMessage("Course deleted successfully");
                response.setId(courseId);

                return response;
            } else {
                DeleteResponse response = new DeleteResponse();
                response.setSuccess(false);
                response.setMessage("Course not found with ID: " + courseId);
                response.setId(courseId);

                return response;
            }
        } catch (Exception e) {
            DeleteResponse response = new DeleteResponse();
            response.setSuccess(false);
            response.setMessage("Error deleting course: " + e.getMessage());
            response.setId(courseId);

            return response;
        }
    }

    @Override
    public RatingResponse rateCourse(Long courseId, RatingRequest ratingRequest) {
        try {
            // Find course by ID
            Optional<CourseEntity> courseOpt = courseRepository.findById(courseId);

            RatingResponse response = new RatingResponse();

            if (courseOpt.isPresent()) {
                CourseEntity courseEntity = courseOpt.get();

                // Get rating from request
                float rating = ratingRequest.getRating().floatValue();
                double currentRating = courseEntity.getRating() != null ? courseEntity.getRating() : 0.0;
                int totalRatings = courseEntity.getTotalRatings() != null ? courseEntity.getTotalRatings() : 0;

                // Calculate new rating
                double newRating = (currentRating * totalRatings + rating) / (totalRatings + 1);
                courseEntity.setRating(newRating);
                courseEntity.setTotalRatings(totalRatings + 1);

                // Save updated entity
                courseRepository.save(courseEntity);

                // Create response
                response.setSuccess(true);
                response.setMessage("Course rated successfully");
                response.setCourseId(courseId);
                response.setRating((float)newRating);
                response.setTotalRatings(totalRatings + 1);
            } else {
                response.setSuccess(false);
                response.setMessage("Course not found with ID: " + courseId);
                response.setCourseId(courseId);
            }

            return response;
        } catch (Exception e) {
            RatingResponse response = new RatingResponse();
            response.setSuccess(false);
            response.setMessage("Error rating course: " + e.getMessage());
            response.setCourseId(courseId);
            return response;
        }
    }

    @Override
    public SearchResponse searchCourses(String query) {
        try {
            // Search for courses
            List<CourseEntity> courseEntities = courseRepository.search(query);

            // Convert entities to DTOs
            List<CourseDTO> courseDTOs = courseEntities.stream()
                    .map(this::convertEntityToDTO)
                    .collect(Collectors.toList());

            // Create response
            SearchResponse response = new SearchResponse();
            response.setSuccess(true);
            response.setMessage("Search results retrieved successfully");
            response.setQuery(query);
            response.setTotalResults(courseDTOs.size());
            response.setResults(courseDTOs);

            return response;
        } catch (Exception e) {
            SearchResponse response = new SearchResponse();
            response.setSuccess(false);
            response.setMessage("Error searching courses: " + e.getMessage());
            response.setQuery(query);
            return response;
        }
    }

    @Override
    public VisitResponse visitCourse(Long courseId) {
        try {
            // Find course by ID
            Optional<CourseEntity> courseOpt = courseRepository.findById(courseId);

            VisitResponse response = new VisitResponse();

            if (courseOpt.isPresent()) {
                CourseEntity courseEntity = courseOpt.get();

                // Update visit count
                int visits = courseEntity.getTotalVisits() != null ? courseEntity.getTotalVisits() : 0;
                courseEntity.setTotalVisits(visits + 1);

                // Save updated entity
                courseRepository.save(courseEntity);

                // Create response
                response.setSuccess(true);
                response.setMessage("Course visit recorded");
                response.setCourseId(courseId);
                response.setVisitCount(visits + 1);
            } else {
                response.setSuccess(false);
                response.setMessage("Course not found with ID: " + courseId);
                response.setCourseId(courseId);
            }

            return response;
        } catch (Exception e) {
            VisitResponse response = new VisitResponse();
            response.setSuccess(false);
            response.setMessage("Error recording course visit: " + e.getMessage());
            response.setCourseId(courseId);
            return response;
        }
    }

    // Helper method to convert entity to DTO
    private CourseDTO convertEntityToDTO(CourseEntity entity) {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setId(entity.getId());
        courseDTO.setTitle(entity.getTitle());
        courseDTO.setDescription(entity.getDescription());
        courseDTO.setInstructor(entity.getInstructor());

        // Convert LocalDateTime to OffsetDateTime
        if (entity.getCreatedAt() != null) {
            courseDTO.setCreatedAt(entity.getCreatedAt().atOffset(ZoneOffset.UTC));
        }

        if (entity.getUpdatedAt() != null) {
            courseDTO.setUpdatedAt(entity.getUpdatedAt().atOffset(ZoneOffset.UTC));
        }

        courseDTO.setRating(entity.getRating() != null ? entity.getRating().floatValue() : 0.0f);
        courseDTO.setTotalRatings(entity.getTotalRatings());
        courseDTO.setTotalVisits(entity.getTotalVisits());

        // Convert tags
        if (entity.getTags() != null) {
            courseDTO.setTags(new ArrayList<>(entity.getTags()));
        }

        return courseDTO;
    }
}