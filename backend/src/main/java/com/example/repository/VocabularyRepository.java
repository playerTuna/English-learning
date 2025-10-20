package com.example.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.Vocabulary;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, UUID> {

    @Override
    Optional<Vocabulary> findById(UUID id);

    Optional<Vocabulary> findByWordAndWordType(String word, Vocabulary.WordType wordType);

    Optional<Vocabulary> findByWord(String word);

    List<Vocabulary> findByTopicName(String topicName);

    Page<Vocabulary> findByTopicName(String topicName, Pageable pageable);

    List<Vocabulary> findByLevel(String level);

    Page<Vocabulary> findByLevel(String level, Pageable pageable);

    List<Vocabulary> findByTopicNameAndLevel(String topicName, String level);

    Page<Vocabulary> findByTopicNameAndLevel(String topicName, String level, Pageable pageable);

    List<Vocabulary> findByWordType(Vocabulary.WordType wordType);

    List<Vocabulary> findByWordContainingOrMeaningContaining(String word, String meaning);

    @Override
    Page<Vocabulary> findAll(Pageable pageable);
}