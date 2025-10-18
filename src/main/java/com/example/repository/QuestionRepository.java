package com.example.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.Question;
import com.example.entity.Test;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
    @Override
    Optional<Question> findById(UUID id);

    List<Question> findByTest(Test test);

    List<Question> findByTestTestId(UUID testId);

    Page<Question> findByTestTestId(UUID testId, Pageable pageable);

    List<Question> findByQuestionTextContaining(String keyword);

    Page<Question> findByQuestionTextContaining(String keyword, Pageable pageable);

    Long countByTestTestId(UUID testId);

    @Query("SELECT q FROM Question q WHERE q.imageUrl IS NOT NULL AND q.test.testId = :testId")
    List<Question> findQuestionsWithImageByTestId(@Param("testId") UUID testId);

    @Query("SELECT q FROM Question q WHERE q.audioUrl IS NOT NULL AND q.test.testId = :testId")
    List<Question> findQuestionsWithAudioByTestId(@Param("testId") UUID testId);

    void deleteByTestTestId(UUID testId);
}