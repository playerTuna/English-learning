package com.example.dto;

import java.time.OffsetDateTime;

public class UserLearningVocabDTO {
    
    private String userId;
    
    private String username;
    
    private String vocabId;
    
    private String word;
    
    private String meaning;
    
    private String wordType;
    
    private String topicName;
    
    private OffsetDateTime lastReviewAt;
    
    private OffsetDateTime nextReviewAt;
    
    private Float easeFactor;
    
    private Long repetitionCount;
    
    private Float successRate;
    
    private Boolean dueForReview;
    
    // Constructors
    public UserLearningVocabDTO() {
    }
    
    public UserLearningVocabDTO(String userId, String username, String vocabId, String word,
                               String meaning, String wordType, String topicName,
                               OffsetDateTime lastReviewAt, OffsetDateTime nextReviewAt,
                               Float easeFactor, Long repetitionCount, Float successRate) {
        this.userId = userId;
        this.username = username;
        this.vocabId = vocabId;
        this.word = word;
        this.meaning = meaning;
        this.wordType = wordType;
        this.topicName = topicName;
        this.lastReviewAt = lastReviewAt;
        this.nextReviewAt = nextReviewAt;
        this.easeFactor = easeFactor;
        this.repetitionCount = repetitionCount;
        this.successRate = successRate;
        
        // Kiểm tra xem từ vựng có cần ôn tập không
        this.dueForReview = nextReviewAt != null && nextReviewAt.isBefore(OffsetDateTime.now());
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
    
    public String getVocabId() {
        return vocabId;
    }
    
    public void setVocabId(String vocabId) {
        this.vocabId = vocabId;
    }
    
    public String getWord() {
        return word;
    }
    
    public void setWord(String word) {
        this.word = word;
    }
    
    public String getMeaning() {
        return meaning;
    }
    
    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
    
    public String getWordType() {
        return wordType;
    }
    
    public void setWordType(String wordType) {
        this.wordType = wordType;
    }
    
    public String getTopicName() {
        return topicName;
    }
    
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
    
    public OffsetDateTime getLastReviewAt() {
        return lastReviewAt;
    }
    
    public void setLastReviewAt(OffsetDateTime lastReviewAt) {
        this.lastReviewAt = lastReviewAt;
    }
    
    public OffsetDateTime getNextReviewAt() {
        return nextReviewAt;
    }
    
    public void setNextReviewAt(OffsetDateTime nextReviewAt) {
        this.nextReviewAt = nextReviewAt;
        this.dueForReview = nextReviewAt != null && nextReviewAt.isBefore(OffsetDateTime.now());
    }
    
    public Float getEaseFactor() {
        return easeFactor;
    }
    
    public void setEaseFactor(Float easeFactor) {
        this.easeFactor = easeFactor;
    }
    
    public Long getRepetitionCount() {
        return repetitionCount;
    }
    
    public void setRepetitionCount(Long repetitionCount) {
        this.repetitionCount = repetitionCount;
    }
    
    public Float getSuccessRate() {
        return successRate;
    }
    
    public void setSuccessRate(Float successRate) {
        this.successRate = successRate;
    }
    
    public Boolean getDueForReview() {
        return dueForReview;
    }
    
    public void updateDueForReview() {
        this.dueForReview = nextReviewAt != null && nextReviewAt.isBefore(OffsetDateTime.now());
    }
}