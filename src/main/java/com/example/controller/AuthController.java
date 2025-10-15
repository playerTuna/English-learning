package com.example.controller;

import com.example.dto.ApiResponseDTO;
import com.example.dto.LoginRequestDTO;
import com.example.dto.LoginResponseDTO;
import com.example.dto.RegisterRequestDTO;
import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.security.JwtService;
import com.example.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            // Authenticate user
            if (!jwtService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDTO<>(false, "Invalid username or password", null));
            }

            // Get user details
            Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDTO<>(false, "User not found", null));
            }

            User user = userOptional.get();

            // Generate JWT token
            String token = jwtUtils.generateToken(loginRequest.getUsername());

            // Create response
            LoginResponseDTO loginResponse = new LoginResponseDTO(
                token,
                user.getUserId().toString(),
                user.getUsername(),
                user.getName(),
                user.getRole().name()
            );

            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Login successful", loginResponse));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDTO<>(false, "Login failed: " + e.getMessage(), null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        try {
            // Check if username already exists
            if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponseDTO<>(false, "Username already exists", null));
            }

            // Create new user
            User newUser = new User();
            newUser.setUsername(registerRequest.getUsername());
            newUser.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
            newUser.setName(registerRequest.getName());
            newUser.setRole(User.UserRole.user); // Default role

            // Save user
            User savedUser = userRepository.save(newUser);

            // Generate JWT token
            String token = jwtUtils.generateToken(savedUser.getUsername());

            // Create response
            LoginResponseDTO loginResponse = new LoginResponseDTO(
                token,
                savedUser.getUserId().toString(),
                savedUser.getUsername(),
                savedUser.getName(),
                savedUser.getRole().name()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(true, "Registration successful", loginResponse));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDTO<>(false, "Registration failed: " + e.getMessage(), null));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponseDTO<Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDTO<>(false, "Invalid token format", null));
            }

            String token = authHeader.substring(7);
            boolean isValid = jwtUtils.validateToken(token);

            if (isValid) {
                String username = jwtUtils.extractUsername(token);
                return ResponseEntity.ok(new ApiResponseDTO<>(true, "Token is valid", username));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDTO<>(false, "Token is invalid or expired", null));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDTO<>(false, "Token validation failed: " + e.getMessage(), null));
        }
    }
}

