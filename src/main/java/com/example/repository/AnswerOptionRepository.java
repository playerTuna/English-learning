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

import com.example.entity.AnswerOption;
import com.example.entity.Question;

@Repository
public interface AnswerOptionRepository extends JpaRepository<AnswerOption, UUID> {
    @Override
    Optional<AnswerOption> findById(UUID id);

    List<AnswerOption> findByQuestion(Question question);

    List<AnswerOption> findByQuestionQuestionId(UUID questionId);

    Page<AnswerOption> findByQuestionQuestionId(UUID questionId, Pageable pageable);

    @Query("SELECT ao FROM AnswerOption ao WHERE ao.question.questionId = :questionId AND ao.isCorrect = true")
    List<AnswerOption> findCorrectAnswersByQuestionId(@Param("questionId") UUID questionId);

    @Query("SELECT ao FROM AnswerOption ao WHERE ao.question.questionId IN :questionIds AND ao.isCorrect = true")
    List<AnswerOption> findCorrectAnswersByQuestionIds(@Param("questionIds") List<UUID> questionIds);

    @Query("SELECT ao FROM AnswerOption ao WHERE ao.question.test.testId = :testId AND ao.isCorrect = true")
    List<AnswerOption> findCorrectAnswersByTestId(@Param("testId") UUID testId);

    Long countByQuestionQuestionId(UUID questionId);

    void deleteByQuestionQuestionId(UUID questionId);
}