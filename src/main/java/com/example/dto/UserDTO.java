package com.example.dto;

import com.example.entity.User.UserRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDTO {
    
    private String userId;
    
    @NotBlank(message = "Username must not be null")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Password must not be null")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    private String name;
    
    private UserRole role;
    
    // Constructor
    public UserDTO() {
    }
    
    public UserDTO(String userId, String username, String name, UserRole role) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.role = role;
    }
    
    // Getters and Setters
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
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}