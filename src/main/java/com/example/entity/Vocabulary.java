package com.example.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
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
@Table(name = "vocabulary", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "topic_id", "word", "word_type" })
})
public class Vocabulary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "vocab_id", updatable = false, nullable = false)
    private UUID vocabId;

    @ManyToOne
    @JoinColumn(name = "topic_id", foreignKey = @ForeignKey(name = "fk_vocabulary_topic"))
    private Topic topic;

    @Column(name = "word", nullable = false)
    private String word;

    @Column(name = "meaning")
    private String meaning;

    @Enumerated(EnumType.STRING)
    @Column(name = "word_type", nullable = false)
    private WordType wordType;

    @Column(name = "example_sentence")
    private String exampleSentence;

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "level", nullable = true)
    private String level;

    public enum WordType {
        noun, verb, adjective, adverb, phrase, other
    }

    public Vocabulary() {
    }

    public Vocabulary(String word, WordType wordType) {
        this.word = word;
        this.wordType = wordType;
    }

    public Vocabulary(Topic topic, String word, String meaning, WordType wordType,
            String exampleSentence, String audioUrl) {
        this.topic = topic;
        this.word = word;
        this.meaning = meaning;
        this.wordType = wordType;
        this.exampleSentence = exampleSentence;
        this.audioUrl = audioUrl;
    }


    public UUID getVocabId() {
        return vocabId;
    }

    public void setVocabId(UUID vocabId) {
        this.vocabId = vocabId;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
