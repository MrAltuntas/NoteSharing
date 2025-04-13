# Note Sharing API

## Project Status & Alignment
https://github.com/MrAltuntas/NoteSharing.git

This submission represents the third assignment in the series, featuring significant improvements:

- **Fully Functional APIs**: login, register, course management, and enrollment endpoints are fully implemented and functional.
- **Complete Many-to-Many Relationship**: A proper many-to-many relationship is established between users and courses, allowing students to enroll in multiple courses and courses to have multiple students.
- **Aligned Project Structure**: The project structure has been refined to closely mirror the instructor’s reference implementation, including updated pom.xml dependencies and plugins. This ensures a well-organized, layered architecture and consistent project dependencies, correcting the discrepancies noted in the first two assignments.
- **Persistent Data Storage**: Data is now stored in a database rather than in memory, using JPA/Hibernate with H2 database.

## Architecture

The application follows a layered architecture pattern:

1. **Controller Layer**: Handles HTTP requests and responses
    - `AuthController`: Authentication operations (login, register)
    - `CourseController`: Course management operations
    - `EnrollmentController`: Course enrollment operations

2. **Service Layer**: Contains business logic
    - `AuthService`: User authentication and registration logic
    - `CourseService`: Course management logic
    - `EnrollmentService`: Course enrollment logic

3. **Repository Layer**: Data access through JPA/Hibernate
    - `UserRepository`: User entity operations
    - `CourseRepository`: Course entity operations

4. **Models**:
    - DTO (Data Transfer Objects): Used for API requests/responses
    - Entity: Database entities with JPA annotations

## Key Features

- User authentication (login/register)
- Course management (CRUD operations)
- Many-to-many relationship between users and courses
- Course ratings and visits tracking
- Search functionality for courses

## Database Schema

The application uses an H2 database with the following key tables:

- `users`: Stores user information
- `courses`: Stores course information
- `enrollments`: Join table for the many-to-many relationship between users and courses
- `course_tags`: Stores tags associated with courses
- `notes`: Stores notes created by users for courses

## Prerequisites

- Java JDK 17 or higher
- Maven 3.6.0 or higher

## Building and Running the Application

### 1. Clone the repository

```bash
git clone [repository-url]
cd [repository-directory]
```

### 2. Build the application

```bash
mvn clean package
```

### 3. Run the application

```bash
java -jar target/notesharing-0.0.1-SNAPSHOT.jar
```

Or using Maven:

```bash
mvn spring-boot:run
```

### 4. Access the application

- The API will be available at: `http://localhost:8080`
- H2 Database Console: `http://localhost:8080/h2-console`
    - JDBC URL: `jdbc:h2:file:./data/notedb`
    - Username: `sa`
    - Password: `password`

## API Documentation

The API is documented using OpenAPI (Swagger). You can access the API documentation at:

```
http://localhost:8080/swagger-ui.html
```

## Many-to-Many Relationship

The application implements a many-to-many relationship between users (students) and courses:

- A student can enroll in multiple courses
- A course can have multiple students enrolled

This relationship is implemented using JPA annotations:

```java
// In User.java (owner side)
@ManyToMany
@JoinTable(
    name = "enrollments",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "course_id")
)
private Set<CourseEntity> enrolledCourses = new HashSet<>();

// In CourseEntity.java (non-owner side)
@ManyToMany(mappedBy = "enrolledCourses")
private Set<User> enrolledStudents = new HashSet<>();
```

## Testing the API

You can test the API using tools like Postman or curl. Here are some example API endpoints:

- `POST /api/v1/auth/register`: Register a new user
- `POST /api/v1/auth/login`: Login and get JWT token
- `GET /api/v1/courses`: Get all courses
- `POST /api/v1/courses/{courseId}/enrollments`: Enroll a student in a course
- `GET /api/v1/courses/{courseId}/enrollments`: Get all students enrolled in a course