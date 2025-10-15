package com.example.dto;

import com.example.entity.Vocabulary.WordType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VocabularyDTO {
    
    private String vocabId;
    
    private String topicId;
    
    private String topicName;
    
    @NotBlank(message = "Từ vựng không được để trống")
    @Size(max = 100, message = "Từ vựng không được vượt quá 100 ký tự")
    private String word;
    
    @Size(max = 500, message = "Nghĩa của từ không được vượt quá 500 ký tự")
    private String meaning;
    
    @NotNull(message = "Loại từ không được để trống")
    private WordType wordType;
    
    @Size(max = 1000, message = "Câu ví dụ không được vượt quá 1000 ký tự")
    private String exampleSentence;
    
    private String audioUrl;
    
    private String level;
    
    // Constructors
    public VocabularyDTO() {
    }
    
    public VocabularyDTO(String vocabId, String word, String meaning, WordType wordType, 
                         String topicId, String topicName, String exampleSentence, 
                         String audioUrl, String level) {
        this.vocabId = vocabId;
        this.word = word;
        this.meaning = meaning;
        this.wordType = wordType;
        this.topicId = topicId;
        this.topicName = topicName;
        this.exampleSentence = exampleSentence;
        this.audioUrl = audioUrl;
        this.level = level;
    }
    
    // Getters and Setters
    public String getVocabId() {
        return vocabId;
    }
    
    public void setVocabId(String vocabId) {
        this.vocabId = vocabId;
    }
    
    public String getTopicId() {
        return topicId;
    }
    
    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
    
    public String getTopicName() {
        return topicName;
    }
    
    public void setTopicName(String topicName) {
        this.topicName = topicName;
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
    
    public WordType getWordType() {
        return wordType;
    }
    
    public void setWordType(WordType wordType) {
        this.wordType = wordType;
    }
    
    public String getExampleSentence() {
        return exampleSentence;
    }
    
    public void setExampleSentence(String exampleSentence) {
        this.exampleSentence = exampleSentence;
    }
    
    public String getAudioUrl() {
        return audioUrl;
    }
    
    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
    
    public String getLevel() {
        return level;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }
}