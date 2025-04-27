# Note Sharing Platform
## Project Status & Alignment

- Backend Repository: https://github.com/MrAltuntas/NoteSharing.git
- Frontend Repository: https://github.com/your-username/NoteSharing-Frontend.git

## Architecture

### Backend (Spring Boot)

The backend follows a layered architecture pattern:

1. **Controller Layer**: Handles HTTP requests and responses
    - `AuthController`: Authentication operations (login, register)
    - `CourseController`: Course management operations
    - `EnrollmentController`: Course enrollment operations
    - `ApiStatisticsController`: API usage statistics operations

2. **Service Layer**: Contains business logic
    - `AuthService`: User authentication and registration logic
    - `CourseService`: Course management logic
    - `EnrollmentService`: Course enrollment logic
    - `ApiStatisticsService`: API usage tracking and analytics logic

3. **Repository Layer**: Data access through JPA/Hibernate
    - `UserRepository`: User entity operations
    - `CourseRepository`: Course entity operations
    - `ApiStatisticsRepository`: API statistics operations

4. **Models**:
    - DTO (Data Transfer Objects): Used for API requests/responses
    - Entity: Database entities with JPA annotations

5. **Interceptors**:
    - `ApiStatisticsInterceptor`: Automatically tracks API usage for all API endpoints

### Frontend (Next.js)

The frontend follows a modern React architecture:

1. **Pages**: Next.js pages corresponding to different routes
    - `register`: User registration page
    - `login`: User login page
    - `course-list`: Course browsing page
    - `create-course`: Course creation page


## Key Features

- User authentication (register, login)
- Course management (CRUD operations)
- Course browsing with search, filtering, and sorting
- API usage statistics and analytics

## Prerequisites

### Backend
- Java JDK 17 or higher
- Maven 3.6.0 or higher

### Frontend
- Node.js 16 or higher
- npm or yarn

## Building and Running the Applications

### Backend Setup

1. Clone the repository
```bash
git clone https://github.com/MrAltuntas/NoteSharing.git
cd NoteSharing
```

2. Build the application
```bash
mvn clean package
```

3. Run the application
```bash
java -jar target/notesharing-0.0.1-SNAPSHOT.jar
```

Or using Maven:
```bash
mvn spring-boot:run
```

The backend API will be available at: `http://localhost:8080`

### Frontend Setup

1. Clone the repository
```bash
git clone https://github.com/your-username/NoteSharing-Frontend.git
cd NoteSharing-Frontend
```

2. Install dependencies
```bash
npm install
# or
yarn install
```

3. Run the development server
```bash
npm run dev
# or
yarn dev
```

The frontend application will be available at: `http://localhost:3000`

## API Documentation

The backend API is documented using OpenAPI (Swagger). You can access the API documentation at:
```
http://localhost:8080/swagger-ui.html
```

## Frontend Pages

| Page | URL | Description |
|------|-----|-------------|
| Home | `/` | Landing page |
| Register | `/register` | User registration page |
| Login | `/login` | User login page |
| Course List | `/course-list` | Browse available courses |
| Create Course | `/create-course` | Create a new course |

## Database Information

The application uses an H2 database with the following configuration:
- H2 Database Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/notedb`
- Username: `sa`
- Password: `password`

## API Statistics

The application includes comprehensive API usage tracking and analytics:

### Features
- Automatic tracking of all API requests
- Records endpoint, HTTP method, IP address, status code, response time, and more
- User-friendly statistics dashboard

### API Statistics Endpoints
- `GET /api/v1/statistics`: Get a summary of API usage including most used endpoints, success rates, response times, and time-based analytics
- `GET /api/v1/statistics/details`: Get detailed API usage records with optional filtering by endpoint, method, status code, date range, and more

### Tracked Data
The system automatically collects the following data for each API request:
- **Endpoint path**: Which API endpoint was accessed
- **HTTP method**: GET, POST, PUT, DELETE, etc.
- **IP address**: Client IP address
- **Status code**: HTTP response status (200, 404, 500, etc.)
- **Response time**: How long the request took to process (in milliseconds)
- **User agent**: Client browser/application information
- **Timestamp**: When the request occurred
- **User ID**: Which user made the request (if authenticated)

### Implementation
The statistics feature is implemented using:
- Spring Interceptors to automatically capture API calls
- JPA/Hibernate for storing statistics data
- Custom analytics for generating insights

## Testing the Application

### Testing the Backend API

You can test the API using tools like Postman or curl. Here are some example API endpoints:

- `POST /api/v1/auth/register`: Register a new user
- `POST /api/v1/auth/login`: Login and get JWT token
- `GET /api/v1/courses`: Get all courses
- `POST /api/v1/courses/{courseId}/enrollments`: Enroll a student in a course
- `GET /api/v1/courses/{courseId}/enrollments`: Get all students enrolled in a course
- `GET /api/v1/statistics`: Get API usage statistics summary
- `GET /api/v1/statistics/details?endpoint=/api/v1/courses`: Get detailed statistics for the courses endpoint

### Testing the Frontend

Access the frontend at http://localhost:3000 and try the following workflow:

1. Register a new account
2. Login with your credentials
3. Browse the course list
4. Create a new course
5. Enroll in courses

## Technologies Used

### Backend
- Spring Boot
- Spring Security with JWT
- JPA/Hibernate
- H2 Database
- Maven
- Spring Interceptors (for API statistics)

### Frontend
- Next.js
- React
- TypeScript
- Material UI
- Tailwind CSS