package io.gozcu.notesharing.service;

import io.gozcu.notesharing.model.Login200Response;
import io.gozcu.notesharing.model.UserDTO;
import io.gozcu.notesharing.model.LoginRequest;
import io.gozcu.notesharing.model.Register200Response;
import io.gozcu.notesharing.model.RegisterRequest;
import io.gozcu.notesharing.model.User;
import io.gozcu.notesharing.model.ValidateToken200Response;
import io.gozcu.notesharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Simple token generation method
    private String generateToken(Long userId) {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ." + userId;
    }

    @Override
    public Register200Response registerUser(RegisterRequest registerRequest) {
        Register200Response response = new Register200Response();

        // Validate request data
        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty() ||
                registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty() ||
                registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {

            response.setSuccess(false);
            response.setMessage("Username, email and password are required");
            return response;
        }

        // Check if username or email already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            response.setSuccess(false);
            response.setMessage("Username already exists");
            return response;
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            response.setSuccess(false);
            response.setMessage("Email already exists");
            return response;
        }

        try {
            // Create new user
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setRole("USER");
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            // Save user to database
            user = userRepository.save(user);

            // Create successful response
            response.setSuccess(true);
            response.setMessage("User registered successfully");
            response.setUserId(user.getId().intValue()); // Convert Long to Integer
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Registration failed: " + e.getMessage());
        }

        return response;
    }

    @Override
    public Login200Response loginUser(LoginRequest loginRequest) {
        Login200Response response = new Login200Response();

        // Validate request data
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {

            response.setSuccess(false);
            response.setMessage("Username and password are required");
            return response;
        }

        try {
            // Find user by username
            Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // Check password
                if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                    // Create user response object using UserDTO (OpenAPI generated class)
                    UserDTO userDto = new UserDTO();
                    userDto.setId(user.getId().intValue()); // Convert Long to Integer
                    userDto.setUsername(user.getUsername());
                    userDto.setEmail(user.getEmail());
                    userDto.setRole(user.getRole());

                    // Generate token
                    String token = generateToken(user.getId());

                    // Create successful response
                    response.setSuccess(true);
                    response.setMessage("Login successful");
                    response.setToken(token);
                    response.setUser(userDto);
                } else {
                    // Password doesn't match
                    response.setSuccess(false);
                    response.setMessage("Invalid password");
                }
            } else {
                // User not found
                response.setSuccess(false);
                response.setMessage("User not found");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Login failed: " + e.getMessage());
        }

        return response;
    }

    @Override
    public ValidateToken200Response validateUserToken() {
        // Simple implementation as per existing code
        ValidateToken200Response response = new ValidateToken200Response();
        response.setValid(true);
        response.setMessage("Token is valid");
        response.setExpires(OffsetDateTime.now().plusDays(7));
        response.setUserId(12345);

        return response;
    }
}