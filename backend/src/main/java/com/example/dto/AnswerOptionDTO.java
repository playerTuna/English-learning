package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AnswerOptionDTO {
    
    private String optionId;
    
    private String questionId;
    
    @NotBlank(message = "Answer option text cannot be blank")
    @Size(max = 500, message = "Answer option text cannot exceed 500 characters")
    private String optionText;
    
    @NotNull(message = "Correct/Incorrect status cannot be null")
    private Boolean isCorrect;
    
    // Constructors
    public AnswerOptionDTO() {
    }
    
    public AnswerOptionDTO(String optionId, String questionId, String optionText, Boolean isCorrect) {
        this.optionId = optionId;
        this.questionId = questionId;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
    }
    
    // Getters and Setters
    public String getOptionId() {
        return optionId;
    }
    
    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }
    
    public String getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
    
    public String getOptionText() {
        return optionText;
    }
    
    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }
    
    public Boolean getIsCorrect() {
        return isCorrect;
    }
    
    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}