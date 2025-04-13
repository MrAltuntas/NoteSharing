package io.gozcu.notesharing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
public class CourseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "instructor")
    private String instructor;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "total_ratings")
    private Integer totalRatings;

    @Column(name = "total_visits")
    private Integer totalVisits;

    @ElementCollection
    @CollectionTable(name = "course_tags", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<NoteEntity> notes = new HashSet<>();

    // Direct many-to-many relationship with User
    @ManyToMany(mappedBy = "enrolledCourses")
    private Set<User> enrolledStudents = new HashSet<>();

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }

    public Integer getTotalVisits() {
        return totalVisits;
    }

    public void setTotalVisits(Integer totalVisits) {
        this.totalVisits = totalVisits;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Set<NoteEntity> getNotes() {
        return notes;
    }

    public void setNotes(Set<NoteEntity> notes) {
        this.notes = notes;
    }

    public Set<User> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(Set<User> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    // Constructors
    public CourseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.rating = 0.0;
        this.totalRatings = 0;
        this.totalVisits = 0;
    }

    public CourseEntity(String title, String description, String instructor) {
        this();
        this.title = title;
        this.description = description;
        this.instructor = instructor;
    }
}