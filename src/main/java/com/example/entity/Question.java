package com.example.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "questions", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "test_id", "question_text" })
})
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "question_id", updatable = false, nullable = false)
    private UUID questionId;

    @ManyToOne(cascade = jakarta.persistence.CascadeType.ALL)
    @JoinColumn(name = "test_id", foreignKey = @jakarta.persistence.ForeignKey(name = "fk_questions_test"))
    private Test test;

    @Column(name = "question_text", nullable = false)
    private String questionText;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "correct_answer_text")
    private String correctAnswerText;

    @Column(name = "created_at", nullable = false)
    private java.time.OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.OffsetDateTime updatedAt;

    public Question() {
    }

    public Question(Test test, String questionText, String imageUrl, String audioUrl) {
        this.test = test;
        this.questionText = questionText;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
    
    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getCorrectAnswerText() {
        return correctAnswerText;
    }

    public void setCorrectAnswerText(String correctAnswerText) {
        this.correctAnswerText = correctAnswerText;
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
}
