package com.example.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.TestResultDTO;
import com.example.entity.Test;
import com.example.entity.TestResult;
import com.example.entity.User;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.TestRepository;
import com.example.repository.TestResultRepository;
import com.example.repository.UserRepository;

@Service
public class TestResultService {

    private final TestResultRepository testResultRepository;
    private final UserRepository userRepository;
    private final TestRepository testRepository;

    @Autowired
    public TestResultService(TestResultRepository testResultRepository,
            UserRepository userRepository,
            TestRepository testRepository) {
        this.testResultRepository = testResultRepository;
        this.userRepository = userRepository;
        this.testRepository = testRepository;
    }

    public List<TestResultDTO> getAllTestResults() {
        return testResultRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TestResultDTO getTestResultById(UUID id) {
        TestResult testResult = testResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test result not found with ID: " + id));
        return convertToDTO(testResult);
    }

    public List<TestResultDTO> getTestResultsByUserId(UUID userId) {
        return testResultRepository.findByUserUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TestResultDTO> getTestResultsByTestId(UUID testId) {
        return testResultRepository.findByTestTestId(testId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TestResultDTO> getTestResultsByUserIdAndTestId(UUID userId, UUID testId) {
        return testResultRepository.findByUserUserIdAndTestTestId(userId, testId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TestResultDTO> getRecentTestResultsByUserId(UUID userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return testResultRepository.findRecentTestResultsByUserId(userId, pageable).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TestResultDTO> getTestResultsByUserIdAndDateRange(UUID userId,
            OffsetDateTime startDate,
            OffsetDateTime endDate) {
        return testResultRepository.findTestResultsByUserIdAndDateRange(userId, startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Double calculateAverageScoreByUserId(UUID userId) {
        return testResultRepository.calculateAverageScoreByUserId(userId);
    }

    public Double calculateAverageScoreByUserIdAndTestId(UUID userId, UUID testId) {
        return testResultRepository.calculateAverageScoreByUserIdAndTestId(userId, testId);
    }

    public Integer findHighestScoreByUserIdAndTestId(UUID userId, UUID testId) {
        return testResultRepository.findHighestScoreByUserIdAndTestId(userId, testId);
    }

    @Transactional
    public TestResultDTO createTestResult(TestResultDTO testResultDTO) {
        // Validate input data
        if (testResultDTO.getCorrectAnswers() == null || testResultDTO.getCorrectAnswers() < 0) {
            throw new IllegalArgumentException("Correct answers must be non-negative");
        }
        if (testResultDTO.getTotalQuestions() == null || testResultDTO.getTotalQuestions() <= 0) {
            throw new IllegalArgumentException("Total questions must be positive");
        }
        if (testResultDTO.getCorrectAnswers() > testResultDTO.getTotalQuestions()) {
            throw new IllegalArgumentException("Correct answers cannot exceed total questions");
        }

        // Kiểm tra userId và testId hợp lệ
        User user = userRepository.findById(UUID.fromString(testResultDTO.getUserId()))
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found with ID: " + testResultDTO.getUserId()));

        Test test = testRepository.findById(UUID.fromString(testResultDTO.getTestId()))
                .orElseThrow(
                        () -> new ResourceNotFoundException("Test not found with ID: " + testResultDTO.getTestId()));

        // Tạo TestResult mới
        TestResult testResult = new TestResult();
        testResult.setUser(user);
        testResult.setTest(test);
        testResult.setCorrectAnswers(testResultDTO.getCorrectAnswers());
        testResult.setTotalQuestions(testResultDTO.getTotalQuestions());

        // Tính điểm số dựa trên số câu đúng / tổng số câu
        int calculatedScore = (int) Math
                .round((double) testResultDTO.getCorrectAnswers() * 100 / testResultDTO.getTotalQuestions());
        testResult.setScore(calculatedScore);

        testResult.setTakenAt(testResultDTO.getTakenAt() != null ? testResultDTO.getTakenAt() : OffsetDateTime.now());

        TestResult savedTestResult = testResultRepository.save(testResult);

        // Chuyển đổi sang DTO để trả về
        return convertToDTO(savedTestResult);
    }

    @Transactional
    public TestResultDTO updateTestResult(UUID id, TestResultDTO testResultDTO) {
        TestResult testResult = testResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test result not found with ID: " + id));

        // Cập nhật thông tin
        if (testResultDTO.getCorrectAnswers() != null) {
            testResult.setCorrectAnswers(testResultDTO.getCorrectAnswers());
        }

        if (testResultDTO.getTotalQuestions() != null) {
            testResult.setTotalQuestions(testResultDTO.getTotalQuestions());
        }

        if (testResultDTO.getTakenAt() != null) {
            testResult.setTakenAt(testResultDTO.getTakenAt());
        }

        // Tính lại điểm số dựa trên số câu đúng / tổng số câu
        if (testResult.getCorrectAnswers() != null && testResult.getTotalQuestions() > 0) {
            int calculatedScore = (int) Math
                    .round((double) testResult.getCorrectAnswers() * 100 / testResult.getTotalQuestions());
            testResult.setScore(calculatedScore);
        }

        TestResult updatedTestResult = testResultRepository.save(testResult);
        return convertToDTO(updatedTestResult);
    }

    @Transactional
    public void deleteTestResult(UUID id) {
        if (!testResultRepository.existsById(id)) {
            throw new ResourceNotFoundException("Test result not found with ID: " + id);
        }
        testResultRepository.deleteById(id);
    }

    @Transactional
    public void deleteTestResultsByUserId(UUID userId) {
        testResultRepository.deleteByUserUserId(userId);
    }

    @Transactional
    public void deleteTestResultsByTestId(UUID testId) {
        testResultRepository.deleteByTestTestId(testId);
    }

    // Helper method to convert Entity to DTO
    private TestResultDTO convertToDTO(TestResult testResult) {
        String testName = "";
        String topicName = "";

        if (testResult.getTest() != null) {
            if (testResult.getTest().getTestType() != null) {
                testName = testResult.getTest().getTestType().toString();
            }

            if (testResult.getTest().getTopic() != null) {
                topicName = testResult.getTest().getTopic().getName();
            }
        }

        return new TestResultDTO(
                testResult.getResultId().toString(),
                testResult.getUser().getUserId().toString(),
                testResult.getUser().getUsername(),
                testResult.getTest().getTestId().toString(),
                testName,
                topicName,
                testResult.getScore(),
                testResult.getCorrectAnswers(),
                testResult.getTotalQuestions(),
                testResult.getTakenAt());
    }
}
