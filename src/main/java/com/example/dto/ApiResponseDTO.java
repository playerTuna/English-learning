package com.example.dto;

import java.time.OffsetDateTime;

public class ApiResponseDTO<T> {
    
    private boolean success;
    
    private String message;
    
    private T data;
    
    private OffsetDateTime timestamp;
    
    // Constructors
    public ApiResponseDTO() {
        this.timestamp = OffsetDateTime.now();
    }
    
    public ApiResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = OffsetDateTime.now();
    }
    
    public ApiResponseDTO(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = OffsetDateTime.now();
    }
    
    // Static factory methods
    public static <T> ApiResponseDTO<T> success(T data) {
        return new ApiResponseDTO<>(true, "Success", data);
    }
    
    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return new ApiResponseDTO<>(true, message, data);
    }
    
    public static <T> ApiResponseDTO<T> error(String message) {
        return new ApiResponseDTO<>(false, message);
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
}