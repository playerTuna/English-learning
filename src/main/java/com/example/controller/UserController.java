package com.example.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.dto.LoginResponseDTO;
import com.example.dto.UserDTO;
import com.example.entity.RefreshToken;
import com.example.service.UserService;
import com.example.service.RefreshTokenService;
import com.example.security.JwtService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @Autowired
    public UserController(UserService userService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    // ====================================================
    // 🔐 LOGIN
    // ====================================================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }

        try {
            UserDTO user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

            String jwtToken = jwtService.generateToken(user.getUsername());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUserId());

            LoginResponseDTO response = new LoginResponseDTO(
                    jwtToken,
                    String.valueOf(user.getUserId()),
                    user.getUsername(),
                    user.getName(),
                    user.getRole() != null ? user.getRole().toString() : null,
                    refreshToken.getToken()
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        if (userDTO.getUsername() == null || userDTO.getPassword() == null) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }

        try {
            UserDTO registeredUser = userService.register(userDTO);

            String jwtToken = jwtService.generateToken(registeredUser.getUsername());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(registeredUser.getUserId());

            LoginResponseDTO response = new LoginResponseDTO(
                    jwtToken,
                    String.valueOf(registeredUser.getUserId()),
                    registeredUser.getUsername(),
                    registeredUser.getName(),
                    registeredUser.getRole() != null ? registeredUser.getRole().toString() : null,
                    refreshToken.getToken()
            );

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            if ("Username already exists".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam("token") String refreshTokenStr) {
        try {
            RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenStr)
                    .map(refreshTokenService::verifyExpiration)
                    .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

            String newJwt = jwtService.generateToken(refreshToken.getUser().getUsername());

            return ResponseEntity.ok(new LoginResponseDTO(
                    newJwt,
                    String.valueOf(refreshToken.getUser().getUserId()),
                    refreshToken.getUser().getUsername(),
                    refreshToken.getUser().getName(),
                    refreshToken.getUser().getRole().toString(),
                    refreshTokenStr
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam("userId") UUID userId) {
        try {
            refreshTokenService.deleteByUserId(userId);
            return ResponseEntity.ok("Logout successful");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Logout failed: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") UUID id, @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
