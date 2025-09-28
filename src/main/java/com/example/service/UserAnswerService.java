package com.example.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.UserAnswerDTO;
import com.example.entity.AnswerOption;
import com.example.entity.Question;
import com.example.entity.TestResult;
import com.example.entity.UserAnswer;
import com.example.repository.AnswerOptionRepository;
import com.example.repository.QuestionRepository;
import com.example.repository.TestResultRepository;
import com.example.repository.UserAnswerRepository;

@Service
public class UserAnswerService {

    private final UserAnswerRepository userAnswerRepository;
    private final TestResultRepository testResultRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final QuestionRepository questionRepository;
    private final QuestionGenerationService questionGenerationService;

    @Autowired
    public UserAnswerService(UserAnswerRepository userAnswerRepository,
            TestResultRepository testResultRepository,
            AnswerOptionRepository answerOptionRepository,
            QuestionRepository questionRepository,
            QuestionGenerationService questionGenerationService) {
        this.userAnswerRepository = userAnswerRepository;
        this.testResultRepository = testResultRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.questionRepository = questionRepository;
        this.questionGenerationService = questionGenerationService;
    }

    public List<UserAnswerDTO> getAllUserAnswers() {
        return userAnswerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserAnswerDTO getUserAnswerById(UUID id) {
        UserAnswer userAnswer = userAnswerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User answer not found with ID: " + id));
        return convertToDTO(userAnswer);
    }

    public List<UserAnswerDTO> getUserAnswersByResultId(UUID resultId) {
        return userAnswerRepository.findByTestResultResultId(resultId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UserAnswerDTO> getCorrectUserAnswersByResultId(UUID resultId) {
        return userAnswerRepository.findByTestResultResultIdAndIsCorrect(resultId, true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UserAnswerDTO> getIncorrectUserAnswersByResultId(UUID resultId) {
        return userAnswerRepository.findByTestResultResultIdAndIsCorrect(resultId, false).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Long countCorrectUserAnswersByResultId(UUID resultId) {
        return userAnswerRepository.countByTestResultResultIdAndIsCorrect(resultId, true);
    }

    public Long countIncorrectUserAnswersByResultId(UUID resultId) {
        return userAnswerRepository.countByTestResultResultIdAndIsCorrect(resultId, false);
    }

    public UserAnswerDTO getUserAnswerByResultIdAndQuestionId(UUID resultId, UUID questionId) {
        UserAnswer userAnswer = userAnswerRepository.findByResultIdAndQuestionId(resultId, questionId)
                .orElseThrow(() -> new RuntimeException(
                        "User answer not found for result ID: " + resultId + " and question ID: " + questionId));
        return convertToDTO(userAnswer);
    }

    @Transactional
    public UserAnswerDTO createUserAnswer(UserAnswerDTO userAnswerDTO) {
        // Kiểm tra resultId và questionId hợp lệ
        TestResult testResult = testResultRepository.findById(UUID.fromString(userAnswerDTO.getResultId()))
                .orElseThrow(
                        () -> new RuntimeException("Test result not found with ID: " + userAnswerDTO.getResultId()));

        Question question = questionRepository.findById(UUID.fromString(userAnswerDTO.getQuestionId()))
                .orElseThrow(
                        () -> new RuntimeException("Question not found with ID: " + userAnswerDTO.getQuestionId()));

        // Tạo UserAnswer mới sử dụng QuestionGenerationService
        UserAnswer userAnswer = questionGenerationService.createUserAnswer(
                question,
                userAnswerDTO.getSelectedOptionId(),
                userAnswerDTO.getTextAnswer());
        userAnswer.setTestResult(testResult);
        userAnswer.setCreatedAt(OffsetDateTime.now());

        UserAnswer savedUserAnswer = userAnswerRepository.save(userAnswer);

        // Chuyển đổi sang DTO để trả về
        return convertToDTO(savedUserAnswer);
    }

    @Transactional
    public UserAnswerDTO updateUserAnswer(UUID id, UserAnswerDTO userAnswerDTO) {
        UserAnswer userAnswer = userAnswerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User answer not found with ID: " + id));

        // Cập nhật thông tin
        if (userAnswerDTO.getSelectedOptionId() != null) {
            AnswerOption selectedOption = answerOptionRepository
                    .findById(UUID.fromString(userAnswerDTO.getSelectedOptionId()))
                    .orElseThrow(() -> new RuntimeException(
                            "Answer option not found with ID: " + userAnswerDTO.getSelectedOptionId()));

            userAnswer.setSelectedOption(selectedOption);
        }

        if (userAnswerDTO.getTextAnswer() != null) {
            userAnswer.setTextAnswer(userAnswerDTO.getTextAnswer());
        }

        if (userAnswerDTO.getIsCorrect() != null) {
            userAnswer.setIsCorrect(userAnswerDTO.getIsCorrect());
        }

        UserAnswer updatedUserAnswer = userAnswerRepository.save(userAnswer);
        return convertToDTO(updatedUserAnswer);
    }

    @Transactional
    public void deleteUserAnswer(UUID id) {
        if (!userAnswerRepository.existsById(id)) {
            throw new RuntimeException("User answer not found with ID: " + id);
        }
        userAnswerRepository.deleteById(id);
    }

    @Transactional
    public void deleteUserAnswersByResultId(UUID resultId) {
        userAnswerRepository.deleteByTestResultResultId(resultId);
    }

    @Transactional
    public List<UserAnswerDTO> createBatchUserAnswers(List<UserAnswerDTO> userAnswerDTOs) {
        return userAnswerDTOs.stream()
                .map(this::createUserAnswer)
                .collect(Collectors.toList());
    }

    // New methods for user and test specific operations
    public List<UserAnswerDTO> getUserAnswersByUserId(UUID userId) {
        return userAnswerRepository.findByTestResultUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UserAnswerDTO> getUserAnswersByTestId(UUID testId) {
        return userAnswerRepository.findByTestResultTestId(testId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UserAnswerDTO> getUserAnswersByUserIdAndTestId(UUID userId, UUID testId) {
        return userAnswerRepository.findByTestResultUserIdAndTestResultTestId(userId, testId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUserAnswersByUserId(UUID userId) {
        userAnswerRepository.deleteByTestResultUserId(userId);
    }

    @Transactional
    public void deleteUserAnswersByTestId(UUID testId) {
        userAnswerRepository.deleteByTestResultTestId(testId);
    }

    // Helper method to convert Entity to DTO
    private UserAnswerDTO convertToDTO(UserAnswer userAnswer) {
        UserAnswerDTO dto = new UserAnswerDTO();
        dto.setAnswerId(userAnswer.getAnswerId().toString());
        dto.setResultId(userAnswer.getTestResult().getResultId().toString());

        if (userAnswer.getQuestion() != null) {
            dto.setQuestionId(userAnswer.getQuestion().getQuestionId().toString());
            dto.setQuestionText(userAnswer.getQuestion().getQuestionText());
            dto.setCorrectAnswerText(userAnswer.getQuestion().getCorrectAnswerText());
        }

        if (userAnswer.getSelectedOption() != null) {
            dto.setSelectedOptionId(userAnswer.getSelectedOption().getOptionId().toString());
            dto.setSelectedOptionText(userAnswer.getSelectedOption().getOptionText());

            // Tìm đáp án đúng cho câu hỏi
            UUID questionId = userAnswer.getQuestion().getQuestionId();
            Optional<AnswerOption> correctOption = answerOptionRepository.findCorrectAnswersByQuestionId(questionId)
                    .stream()
                    .findFirst();

            if (correctOption.isPresent()) {
                dto.setCorrectOptionId(correctOption.get().getOptionId().toString());
                dto.setCorrectOptionText(correctOption.get().getOptionText());
            }
        }

        dto.setTextAnswer(userAnswer.getTextAnswer());
        dto.setIsCorrect(userAnswer.getIsCorrect());

        return dto;
    }
}