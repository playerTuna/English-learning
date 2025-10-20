package com.example.dto;

public class UserAnswerDTO {
    
    private String answerId;
    
    private String resultId;
    
    private String questionId;
    
    private String questionText;
    
    private String selectedOptionId;
    
    private String selectedOptionText;
    
    private String textAnswer;
    
    private Boolean isCorrect;
    
    private String correctOptionId;
    
    private String correctOptionText;
    
    private String correctAnswerText;
    
    // Constructors
    public UserAnswerDTO() {
    }
    
    public UserAnswerDTO(String answerId, String resultId, String questionId, String questionText,
                        String selectedOptionId, String selectedOptionText, String textAnswer, Boolean isCorrect) {
        this.answerId = answerId;
        this.resultId = resultId;
        this.questionId = questionId;
        this.questionText = questionText;
        this.selectedOptionId = selectedOptionId;
        this.selectedOptionText = selectedOptionText;
        this.textAnswer = textAnswer;
        this.isCorrect = isCorrect;
    }
    
    // Getters and Setters
    public String getAnswerId() {
        return answerId;
    }
    
    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }
    
    public String getResultId() {
        return resultId;
    }
    
    public void setResultId(String resultId) {
        this.resultId = resultId;
    }
    
    public String getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public String getSelectedOptionId() {
        return selectedOptionId;
    }
    
    public void setSelectedOptionId(String selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }
    
    public String getSelectedOptionText() {
        return selectedOptionText;
    }
    
    public void setSelectedOptionText(String selectedOptionText) {
        this.selectedOptionText = selectedOptionText;
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
    
    public String getCorrectOptionId() {
        return correctOptionId;
    }
    
    public void setCorrectOptionId(String correctOptionId) {
        this.correctOptionId = correctOptionId;
    }
    
    public String getCorrectOptionText() {
        return correctOptionText;
    }
    
    public void setCorrectOptionText(String correctOptionText) {
        this.correctOptionText = correctOptionText;
    }
    
    public String getCorrectAnswerText() {
        return correctAnswerText;
    }
    
    public void setCorrectAnswerText(String correctAnswerText) {
        this.correctAnswerText = correctAnswerText;
    }
}