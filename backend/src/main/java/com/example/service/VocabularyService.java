package com.example.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Vocabulary;
import com.example.entity.Vocabulary.WordType;
import com.example.repository.TopicRepository;
import com.example.repository.VocabularyRepository;

@Service
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    private final TopicRepository topicRepository;

    @Autowired
    public VocabularyService(VocabularyRepository vocabularyRepository, TopicRepository topicRepository) {
        this.vocabularyRepository = vocabularyRepository;
        this.topicRepository = topicRepository;
    }

    /**
     * Tạo từ vựng mới
     */
    @Transactional
    public Vocabulary createVocabulary(Vocabulary vocabulary) {
        // Kiểm tra từ và loại từ đã tồn tại chưa
        Optional<Vocabulary> existingVocab = vocabularyRepository.findByWordAndWordType(
                vocabulary.getWord(), vocabulary.getWordType());

        if (existingVocab.isPresent()) {
            throw new RuntimeException("Vocabulary already exists with the same word and word type");
        }

        return vocabularyRepository.save(vocabulary);
    }

    /**
     * Lấy từ vựng theo ID
     */
    public Vocabulary getVocabularyById(UUID id) {
        return vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vocabulary not found with id: " + id));
    }

    /**
     * Lấy từ vựng theo từ
     */
    public Vocabulary getVocabularyByWord(String word) {
        return vocabularyRepository.findByWord(word)
                .orElseThrow(() -> new RuntimeException("Vocabulary not found with word: " + word));
    }

    /**
     * Cập nhật từ vựng
     */
    @Transactional
    public Vocabulary updateVocabulary(UUID id, Vocabulary vocabularyDetails) {
        Vocabulary vocabulary = getVocabularyById(id);

        // Cập nhật thông tin
        if (vocabularyDetails.getMeaning() != null) {
            vocabulary.setMeaning(vocabularyDetails.getMeaning());
        }
        if (vocabularyDetails.getExampleSentence() != null) {
            vocabulary.setExampleSentence(vocabularyDetails.getExampleSentence());
        }
        if (vocabularyDetails.getAudioUrl() != null) {
            vocabulary.setAudioUrl(vocabularyDetails.getAudioUrl());
        }
        if (vocabularyDetails.getTopic() != null) {
            vocabulary.setTopic(vocabularyDetails.getTopic());
        }

        return vocabularyRepository.save(vocabulary);
    }

    /**
     * Xóa từ vựng
     */
    @Transactional
    public void deleteVocabulary(UUID id) {
        if (!vocabularyRepository.existsById(id)) {
            throw new RuntimeException("Vocabulary not found with id: " + id);
        }
        vocabularyRepository.deleteById(id);
    }

    /**
     * Lấy tất cả từ vựng
     */
    public List<Vocabulary> getAllVocabulary() {
        return vocabularyRepository.findAll();
    }

    /**
     * Lấy danh sách từ vựng theo chủ đề
     */
    public List<Vocabulary> getVocabularyByTopic(String topicName) {
        return vocabularyRepository.findByTopicName(topicName);
    }

    /**
     * Tìm kiếm từ vựng theo từ khóa
     */
    public List<Vocabulary> searchVocabulary(String word, String meaning, WordType wordType) {
        if (wordType != null) {
            return vocabularyRepository.findByWordType(wordType);
        }
        if (word != null || meaning != null) {
            return vocabularyRepository.findByWordContainingOrMeaningContaining(
                    word != null ? word : "",
                    meaning != null ? meaning : "");
        }
        return getAllVocabulary();
    }

    /**
     * Lấy danh sách từ vựng theo level
     */
    public List<Vocabulary> getVocabulariesByLevel(String level) {
        return vocabularyRepository.findByLevel(level);
    }

    /**
     * Lấy danh sách từ vựng theo chủ đề và level
     */
    public List<Vocabulary> getVocabulariesByTopicAndLevel(String topic, String level) {
        return vocabularyRepository.findByTopicNameAndLevel(topic, level);
    }

    /**
     * Lấy tất cả từ vựng với phân trang
     */
    public Page<Vocabulary> getAllVocabularyPaginated(Pageable pageable) {
        return vocabularyRepository.findAll(pageable);
    }

    /**
     * Tìm kiếm từ vựng theo chủ đề với phân trang
     */
    public Page<Vocabulary> getVocabularyByTopicPaginated(String topicName, Pageable pageable) {
        return vocabularyRepository.findByTopicName(topicName, pageable);
    }

    /**
     * Tìm kiếm từ vựng theo level với phân trang
     */
    public Page<Vocabulary> getVocabulariesByLevelPaginated(String level, Pageable pageable) {
        return vocabularyRepository.findByLevel(level, pageable);
    }

    /**
     * Tìm kiếm từ vựng theo chủ đề và level với phân trang
     */
    public Page<Vocabulary> getVocabulariesByTopicAndLevelPaginated(String topic, String level, Pageable pageable) {
        return vocabularyRepository.findByTopicNameAndLevel(topic, level, pageable);
    }
}