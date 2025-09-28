package com.example.repository;

import com.example.entity.Question;
import com.example.entity.Test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
    @Override
    Optional<Question> findById(UUID id);

    List<Question> findByTest(Test test);

    List<Question> findByTestTestId(UUID testId);

    Page<Question> findByTestTestId(UUID testId, Pageable pageable);

    // Tìm câu hỏi theo từ khóa trong questionText
    List<Question> findByQuestionTextContaining(String keyword);

    Page<Question> findByQuestionTextContaining(String keyword, Pageable pageable);

    // Đếm số câu hỏi trong bài test
    Long countByTestTestId(UUID testId);

    // Tìm câu hỏi có hình ảnh
    @Query("SELECT q FROM Question q WHERE q.imageUrl IS NOT NULL AND q.test.testId = :testId")
    List<Question> findQuestionsWithImageByTestId(@Param("testId") UUID testId);

    // Tìm câu hỏi có audio
    @Query("SELECT q FROM Question q WHERE q.audioUrl IS NOT NULL AND q.test.testId = :testId")
    List<Question> findQuestionsWithAudioByTestId(@Param("testId") UUID testId);

    // Xóa câu hỏi theo testId
    void deleteByTestTestId(UUID testId);
}