package com.example.entity;

import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import java.util.UUID;

@Table(name = "answer_options", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "question_id", "option_text" })
})
@Entity
public class AnswerOption {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "option_id", updatable = false, nullable = false)
    private UUID optionId;

    @ManyToOne(cascade = jakarta.persistence.CascadeType.ALL)
    @JoinColumn(name = "question_id", foreignKey = @jakarta.persistence.ForeignKey(name = "fk_answer_options_question"))
    private Question question;

    @Column(name = "option_text", nullable = false)
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect;

    @Column(name = "created_at", nullable = false)
    private java.time.OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.OffsetDateTime updatedAt;

    public AnswerOption() {
    }

    public AnswerOption(Question question, String optionText, boolean isCorrect) {
        this.question = question;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = java.time.OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = java.time.OffsetDateTime.now();
    }

    public UUID getOptionId() {
        return optionId;
    }

    public void setOptionId(UUID optionId) {
        this.optionId = optionId;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public java.time.OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public java.time.OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.time.OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}