package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import java.util.UUID;

import org.hibernate.annotations.Check;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
@Table(name = "test")
public class Test {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "test_id", updatable = false, nullable = false)
    private UUID testId;

    @ManyToOne
    @JoinColumn(name = "topic_id", foreignKey = @jakarta.persistence.ForeignKey(name = "fk_tests_topic"))
    private Topic topic;

    @Enumerated(EnumType.STRING)
    @Column(name = "test_type", nullable = false)
    private TestType testType;

    @Column(name = "num_questions", nullable = false)
    private Integer numQuestions;
    @Check(constraints = "num_questions > 0")

    @Column(name = "created_at", nullable = false)
    private java.time.OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.OffsetDateTime updatedAt;

    public enum TestType {
        multiple_choice, fill_in_the_blank, matching, true_false
    }

    public Test() {
    }

    public Test(Topic topic, TestType testType, Integer numQuestions, java.time.OffsetDateTime createdAt) {
        this.topic = topic;
        this.testType = testType;
        this.numQuestions = numQuestions;
        this.createdAt = createdAt;
    }

    public UUID getTestId() {
        return testId;
    }

    public void setTestId(UUID testId) {
        this.testId = testId;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
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
