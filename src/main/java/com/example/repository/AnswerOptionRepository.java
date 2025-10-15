package com.example.repository;

import com.example.entity.AnswerOption;
import com.example.entity.Question;

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
public interface AnswerOptionRepository extends JpaRepository<AnswerOption, UUID> {
    @Override
    Optional<AnswerOption> findById(UUID id);

    List<AnswerOption> findByQuestion(Question question);

    List<AnswerOption> findByQuestionQuestionId(UUID questionId);

    Page<AnswerOption> findByQuestionQuestionId(UUID questionId, Pageable pageable);

    // Tìm đáp án đúng của câu hỏi
    @Query("SELECT ao FROM AnswerOption ao WHERE ao.question.questionId = :questionId AND ao.isCorrect = true")
    List<AnswerOption> findCorrectAnswersByQuestionId(@Param("questionId") UUID questionId);

    // Tìm đáp án đúng cho nhiều câu hỏi
    @Query("SELECT ao FROM AnswerOption ao WHERE ao.question.questionId IN :questionIds AND ao.isCorrect = true")
    List<AnswerOption> findCorrectAnswersByQuestionIds(@Param("questionIds") List<UUID> questionIds);

    // Tìm tất cả đáp án đúng trong một bài test
    @Query("SELECT ao FROM AnswerOption ao WHERE ao.question.test.testId = :testId AND ao.isCorrect = true")
    List<AnswerOption> findCorrectAnswersByTestId(@Param("testId") UUID testId);

    // Đếm số lượng đáp án cho một câu hỏi
    Long countByQuestionQuestionId(UUID questionId);

    // Xóa tất cả đáp án của một câu hỏi
    void deleteByQuestionQuestionId(UUID questionId);
}