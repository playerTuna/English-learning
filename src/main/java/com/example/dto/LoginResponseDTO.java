package com.example.dto;

public class LoginResponseDTO {

    private String token;
    private String userId;
    private String username;
    private String name;
    private String role;
    private String refreshToken; // 👈 thêm trường này

    // Constructors
    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token, String userId, String username, String name, String role, String refreshToken) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.role = role;
        this.refreshToken = refreshToken;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
