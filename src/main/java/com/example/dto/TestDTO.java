package com.example.dto;

import java.time.OffsetDateTime;
import java.util.List;

import com.example.entity.Test.TestType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class TestDTO {

    private String testId;

    private String topicId;

    private String topicName;

    @NotNull(message = "Test type must not be null")
    private TestType testType;

    @NotNull(message = "Number question must not be null")
    @Min(value = 1, message = "Ammount of question must be greater than 0")
    private Integer numQuestions;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    private List<QuestionDTO> questions;

    // Constructors
    public TestDTO() {
    }

    public TestDTO(String testId, String topicId, String topicName, TestType testType,
            Integer numQuestions, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.testId = testId;
        this.topicId = topicId;
        this.topicName = topicName;
        this.testType = testType;
        this.numQuestions = numQuestions;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public TestType getTestType() {
        return testType;
    }

    public void setTestType(TestType testType) {
        this.testType = testType;
    }

    public Integer getNumQuestions() {
        return numQuestions;
    }

    public void setNumQuestions(Integer numQuestions) {
        this.numQuestions = numQuestions;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDTO> questions) {
        this.questions = questions;
    }
}
