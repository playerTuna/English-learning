package com.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import java.util.UUID;

@Entity
@Table(name = "user_answer")
public class UserAnswer {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    @Column(name = "answer_id", updatable = false, nullable = false)
    private UUID answerId;

    @ManyToOne
    @JoinColumn(name = "result_id", foreignKey = @jakarta.persistence.ForeignKey(name = "fk_user_answer_test_result"))
    private TestResult testResult;

    @ManyToOne
    @JoinColumn(name = "question_id", foreignKey = @jakarta.persistence.ForeignKey(name = "fk_user_answer_question"))
    private Question question;

    @ManyToOne(optional = true) 
    @JoinColumn(name = "selected_option_id", nullable = true,
            foreignKey = @jakarta.persistence.ForeignKey(name = "fk_user_answer_answer_option"))
    private AnswerOption selectedOption;

    @Column(name = "text_answer")
    private String textAnswer;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "created_at", nullable = false)
    private java.time.OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.OffsetDateTime updatedAt;

    public UserAnswer() {
    }

    public UserAnswer(TestResult testResult, AnswerOption selectedOption, Boolean isCorrect,
            java.time.OffsetDateTime createdAt) {
        this.testResult = testResult;
        this.selectedOption = selectedOption;
        this.isCorrect = isCorrect;
        this.createdAt = createdAt;
    }

    public UUID getAnswerId() {
        return answerId;
    }

    public void setAnswerId(UUID answerId) {
        this.answerId = answerId;
    }

    public TestResult getTestResult() {
        return testResult;
    }

    public void setTestResult(TestResult testResult) {
        this.testResult = testResult;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public AnswerOption getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(AnswerOption selectedOption) {
        this.selectedOption = selectedOption;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
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

    @PrePersist
    protected void onCreate() {
        this.createdAt = java.time.OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = java.time.OffsetDateTime.now();
    }

}
