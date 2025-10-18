package com.example.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.ApiResponseDTO;
import com.example.dto.LoginRequestDTO;
import com.example.dto.LoginResponseDTO;
import com.example.dto.RegisterRequestDTO;
import com.example.entity.RefreshToken;
import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.security.JwtService;
import com.example.security.JwtUtils;
import com.example.service.RefreshTokenService;

import jakarta.validation.Valid;

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

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            if (!jwtService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponseDTO<>(false, "Invalid username or password", null));
            }

            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String accessToken = jwtUtils.generateToken(user.getUsername());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUserId());

            LoginResponseDTO response = new LoginResponseDTO(
                    accessToken,
                    user.getUserId().toString(),
                    user.getUsername(),
                    user.getName(),
                    user.getRole().name(),
                    refreshToken.getToken()
            );

            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Login successful", response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDTO<>(false, "Login failed: " + e.getMessage(), null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        try {
            if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponseDTO<>(false, "Username already exists", null));
            }

            User newUser = new User();
            newUser.setUsername(registerRequest.getUsername());
            newUser.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
            newUser.setName(registerRequest.getName());
            newUser.setRole(User.UserRole.user);

            User savedUser = userRepository.save(newUser);

            String accessToken = jwtUtils.generateToken(savedUser.getUsername());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getUserId());

            LoginResponseDTO response = new LoginResponseDTO(
                    accessToken,
                    savedUser.getUserId().toString(),
                    savedUser.getUsername(),
                    savedUser.getName(),
                    savedUser.getRole().name(),
                    refreshToken.getToken()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseDTO<>(true, "Registration successful", response));

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

            if (!isValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponseDTO<>(false, "Token is invalid or expired", null));
            }

            String username = jwtUtils.extractUsername(token);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Token is valid", username));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDTO<>(false, "Token validation failed: " + e.getMessage(), null));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshTokenStr = request.get("refreshToken");
            if (refreshTokenStr == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponseDTO<>(false, "Refresh token is required", null));
            }

            RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenStr)
                    .map(refreshTokenService::verifyExpiration)
                    .orElseThrow(() -> new RuntimeException("Invalid or expired refresh token"));

            String newAccessToken = jwtUtils.generateToken(refreshToken.getUser().getUsername());

            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Token refreshed",
                    Map.of("accessToken", newAccessToken)));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDTO<>(false, "Refresh token failed: " + e.getMessage(), null));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO<String>> logoutUser(@RequestBody Map<String, String> request) {
        try {
            UUID userId = UUID.fromString(request.get("userId"));
            refreshTokenService.deleteByUserId(userId);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Logout successful", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDTO<>(false, "Logout failed: " + e.getMessage(), null));
        }
    }
}
