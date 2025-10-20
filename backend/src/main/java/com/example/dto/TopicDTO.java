package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TopicDTO {
    
    private String topicId;
    
    @NotBlank(message = "Topic name must not be null")
    @Size(max = 100, message = "Topic name cannot exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    private Long vocabularyCount;
    
    private Long testCount;
    
    // Constructors
    public TopicDTO() {
    }
    
    public TopicDTO(String topicId, String name, String description) {
        this.topicId = topicId;
        this.name = name;
        this.description = description;
    }
    
    public TopicDTO(String topicId, String name, String description, Long vocabularyCount, Long testCount) {
        this.topicId = topicId;
        this.name = name;
        this.description = description;
        this.vocabularyCount = vocabularyCount;
        this.testCount = testCount;
    }
    
    // Getters and Setters
    public String getTopicId() {
        return topicId;
    }
    
    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getVocabularyCount() {
        return vocabularyCount;
    }
    
    public void setVocabularyCount(Long vocabularyCount) {
        this.vocabularyCount = vocabularyCount;
    }
    
    public Long getTestCount() {
        return testCount;
    }
    
    public void setTestCount(Long testCount) {
        this.testCount = testCount;
    }
}