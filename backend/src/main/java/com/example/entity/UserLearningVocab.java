package com.example.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_learning_vocab")
public class UserLearningVocab {
    
    @EmbeddedId
    private UserLearningVocabId id;
    
    @MapsId("userId")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_learning_vocab_user"))
    private User user;
    
    @MapsId("vocabId")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vocab_id", foreignKey = @ForeignKey(name = "fk_user_learning_vocab_vocabulary"))
    private Vocabulary vocabulary;
    
    @Column(name = "last_review_at")
    private OffsetDateTime lastReviewAt;
    
    @Column(name = "next_review_at")
    private OffsetDateTime nextReviewAt;
    
    @Column(name = "ease_factor")
    private Float easeFactor = 2.5f;
    
    @Column(name = "repetition_count")
    private Long repetitionCount = 0L;
    
    @Column(name = "success_rate")
    private Float successRate = 0.0f;
    
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
    
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
    
    public UserLearningVocab() {
    }
    
    public UserLearningVocab(User user, Vocabulary vocabulary) {
        this.id = new UserLearningVocabId(user.getUserId(), vocabulary.getVocabId());
        this.user = user;
        this.vocabulary = vocabulary;
        this.lastReviewAt = OffsetDateTime.now();
    }
    
    @Embeddable
    public static class UserLearningVocabId implements java.io.Serializable {
        
        @Column(name = "user_id")
        private UUID userId;
        
        @Column(name = "vocab_id")
        private UUID vocabId;
        
        public UserLearningVocabId() {
        }
        
        public UserLearningVocabId(UUID userId, UUID vocabId) {
            this.userId = userId;
            this.vocabId = vocabId;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserLearningVocabId that = (UserLearningVocabId) o;
            return userId.equals(that.userId) && vocabId.equals(that.vocabId);
        }
        
        @Override
        public int hashCode() {
            return userId.hashCode() ^ vocabId.hashCode();
        }
        
        public UUID getUserId() {
            return userId;
        }
        
        public void setUserId(UUID userId) {
            this.userId = userId;
        }
        
        public UUID getVocabId() {
            return vocabId;
        }
        
        public void setVocabId(UUID vocabId) {
            this.vocabId = vocabId;
        }
    }
    
    public UserLearningVocabId getId() {
        return id;
    }
    
    public void setId(UserLearningVocabId id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Vocabulary getVocabulary() {
        return vocabulary;
    }
    
    public void setVocabulary(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
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
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.lastReviewAt == null) {
            this.lastReviewAt = this.createdAt;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}