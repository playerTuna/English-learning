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
import com.example.entity.TestResult;
import com.example.entity.UserAnswer;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, UUID> {

    @Override
    Optional<UserAnswer> findById(UUID id);

    List<UserAnswer> findByTestResult(TestResult testResult);

    List<UserAnswer> findByTestResultResultId(UUID resultId);

    Page<UserAnswer> findByTestResultResultId(UUID resultId, Pageable pageable);

    List<UserAnswer> findBySelectedOption(AnswerOption selectedOption);

    // Tìm câu trả lời đúng/sai của user trong một bài test
    List<UserAnswer> findByTestResultResultIdAndIsCorrect(UUID resultId, Boolean isCorrect);

    // Đếm số câu trả lời đúng/sai trong một bài test
    Long countByTestResultResultIdAndIsCorrect(UUID resultId, Boolean isCorrect);

    // Tìm câu trả lời cho câu hỏi cụ thể trong bài test
    @Query("SELECT ua FROM UserAnswer ua WHERE ua.testResult.resultId = :resultId AND ua.selectedOption.question.questionId = :questionId")
    Optional<UserAnswer> findByResultIdAndQuestionId(@Param("resultId") UUID resultId,
            @Param("questionId") UUID questionId);

    // Thống kê tỷ lệ đúng/sai theo câu hỏi
    @Query("SELECT ua.selectedOption.question.questionId, COUNT(ua), SUM(CASE WHEN ua.isCorrect = true THEN 1 ELSE 0 END) "
            + "FROM UserAnswer ua WHERE ua.testResult.test.testId = :testId "
            + "GROUP BY ua.selectedOption.question.questionId")
    List<Object[]> countCorrectAnswersByQuestion(@Param("testId") UUID testId);

    // Xóa câu trả lời theo resultId
    void deleteByTestResultResultId(UUID resultId);

    // Tìm câu trả lời theo userId
    @Query("SELECT ua FROM UserAnswer ua WHERE ua.testResult.user.userId = :userId")
    List<UserAnswer> findByTestResultUserId(@Param("userId") UUID userId);

    // Tìm câu trả lời theo testId
    @Query("SELECT ua FROM UserAnswer ua WHERE ua.testResult.test.testId = :testId")
    List<UserAnswer> findByTestResultTestId(@Param("testId") UUID testId);

    // Tìm câu trả lời theo userId và testId
    @Query("SELECT ua FROM UserAnswer ua WHERE ua.testResult.user.userId = :userId AND ua.testResult.test.testId = :testId")
    List<UserAnswer> findByTestResultUserIdAndTestResultTestId(
            @Param("userId") UUID userId,
            @Param("testId") UUID testId);

    // Xóa câu trả lời theo userId
    @Query("DELETE FROM UserAnswer ua WHERE ua.testResult.user.userId = :userId")
    void deleteByTestResultUserId(@Param("userId") UUID userId);

    // Xóa câu trả lời theo testId  
    @Query("DELETE FROM UserAnswer ua WHERE ua.testResult.test.testId = :testId")
    void deleteByTestResultTestId(@Param("testId") UUID testId);
}
