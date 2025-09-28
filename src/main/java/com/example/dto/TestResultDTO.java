package com.example.dto;

import java.time.OffsetDateTime;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;

public class TestResultDTO {
    
    private String resultId;
    
    private String userId;
    
    private String username;
    
    private String testId;
    
    private String testName;
    
    private String topicName;
    
    private Integer score;

    @Min(value = 0, message = "Correct answers must be non-negative")
    private Integer correctAnswers;
    
    @Min(value = 1, message = "Total questions must be at least 1")
    private Integer totalQuestions;
    
    private Float percentage;
    
    @PastOrPresent(message = "Taken date cannot be in the future")
    private OffsetDateTime takenAt;
    
    private List<UserAnswerDTO> userAnswers;
    
    // Constructors
    public TestResultDTO() {
    }
    
    public TestResultDTO(String resultId, String userId, String username, String testId, 
                         String testName, String topicName, Integer score, Integer correctAnswers, Integer totalQuestions, 
                         OffsetDateTime takenAt) {
        this.resultId = resultId;
        this.userId = userId;
        this.username = username;
        this.testId = testId;
        this.testName = testName;
        this.topicName = topicName;
        this.score = score;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.takenAt = takenAt;
        
        // Tính phần trăm dựa trên số câu đúng / tổng số câu
        if (totalQuestions != null && totalQuestions > 0 && correctAnswers != null) {
            this.percentage = (float) correctAnswers * 100 / totalQuestions;
        } else {
            this.percentage = 0.0f;
        }
    }
    
    // Getters and Setters
    public String getResultId() {
        return resultId;
    }
    
    public void setResultId(String resultId) {
        this.resultId = resultId;
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
    
    public String getTestId() {
        return testId;
    }
    
    public void setTestId(String testId) {
        this.testId = testId;
    }
    
    public String getTestName() {
        return testName;
    }
    
    public void setTestName(String testName) {
        this.testName = testName;
    }
    
    public String getTopicName() {
        return topicName;
    }
    
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
        if (totalQuestions != null && totalQuestions > 0 && correctAnswers != null) {
            this.percentage = (float) correctAnswers * 100 / totalQuestions;
        }
    }
    
    public Integer getCorrectAnswers() {
        return correctAnswers;
    }
    
    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
        if (totalQuestions != null && totalQuestions > 0 && correctAnswers != null) {
            this.percentage = (float) correctAnswers * 100 / totalQuestions;
        }
    }
    
    public Integer getTotalQuestions() {
        return totalQuestions;
    }
    
    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
        // Recalculate percentage when totalQuestions changes
        if (totalQuestions != null && totalQuestions > 0 && correctAnswers != null) {
            this.percentage = (float) correctAnswers * 100 / totalQuestions;
        }
    }
    
    public Float getPercentage() {
        return percentage;
    }
    
    public OffsetDateTime getTakenAt() {
        return takenAt;
    }
    
    public void setTakenAt(OffsetDateTime takenAt) {
        this.takenAt = takenAt;
    }
    
    public List<UserAnswerDTO> getUserAnswers() {
        return userAnswers;
    }
    
    public void setUserAnswers(List<UserAnswerDTO> userAnswers) {
        this.userAnswers = userAnswers;
    }
}