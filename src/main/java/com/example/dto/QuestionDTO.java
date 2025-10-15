package com.example.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class QuestionDTO {

    private String questionId;

    private String testId;

    @NotBlank(message = "Question text cannot be blank")
    @Size(max = 1000, message = "Question text cannot exceed 1000 characters")
    private String questionText;

    private String imageUrl;

    private String audioUrl;

    private String correctAnswerText;

    private List<AnswerOptionDTO> options;

    // Constructors
    public QuestionDTO() {
    }

    public QuestionDTO(String questionId, String testId, String questionText,
            String imageUrl, String audioUrl, String correctAnswerText) {
        this.questionId = questionId;
        this.testId = testId;
        this.questionText = questionText;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.correctAnswerText = correctAnswerText;
    }

    // Getters and Setters
    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
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

    public List<AnswerOptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<AnswerOptionDTO> options) {
        this.options = options;
    }
}
