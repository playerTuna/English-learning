package com.example.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.AnswerOptionDTO;
import com.example.dto.QuestionDTO;
import com.example.entity.Question;
import com.example.entity.Test;
import com.example.repository.QuestionRepository;
import com.example.repository.TestRepository;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;
    private final AnswerOptionService answerOptionService;

    @Autowired
    public QuestionService(QuestionRepository questionRepository,
            TestRepository testRepository,
            AnswerOptionService answerOptionService) {
        this.questionRepository = questionRepository;
        this.testRepository = testRepository;
        this.answerOptionService = answerOptionService;
    }

    public List<QuestionDTO> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public QuestionDTO getQuestionById(UUID id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + id));

        QuestionDTO questionDTO = convertToDTO(question);

        List<AnswerOptionDTO> options = answerOptionService.getAnswerOptionsByQuestionId(id);
        questionDTO.setOptions(options);

        return questionDTO;
    }

    public List<QuestionDTO> searchQuestionsByText(String keyword) {
        return questionRepository.findByQuestionTextContaining(keyword)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<QuestionDTO> getQuestionsByTestId(UUID testId) {
        List<QuestionDTO> questions = questionRepository.findByTestTestId(testId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        for (QuestionDTO questionDTO : questions) {
            List<AnswerOptionDTO> options = answerOptionService.getAnswerOptionsByQuestionId(
                    UUID.fromString(questionDTO.getQuestionId()));
            questionDTO.setOptions(options);
        }

        return questions;
    }

    public List<QuestionDTO> getQuestionsWithImageByTestId(UUID testId) {
        return questionRepository.findQuestionsWithImageByTestId(testId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<QuestionDTO> getQuestionsWithAudioByTestId(UUID testId) {
        return questionRepository.findQuestionsWithAudioByTestId(testId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public QuestionDTO createQuestion(QuestionDTO questionDTO) {
        // Kiểm tra testId hợp lệ
        Test test = testRepository.findById(UUID.fromString(questionDTO.getTestId()))
                .orElseThrow(() -> new RuntimeException("Test not found with ID: " + questionDTO.getTestId()));

        // Tạo Question mới
        Question question = new Question();
        question.setTest(test);
        question.setQuestionText(questionDTO.getQuestionText());
        question.setImageUrl(questionDTO.getImageUrl());
        question.setAudioUrl(questionDTO.getAudioUrl());

        Question savedQuestion = questionRepository.save(question);

        QuestionDTO savedQuestionDTO = convertToDTO(savedQuestion);

        // Nếu có các đáp án đi kèm, lưu lại
        if (questionDTO.getOptions() != null && !questionDTO.getOptions().isEmpty()) {
            List<AnswerOptionDTO> savedOptions = questionDTO.getOptions().stream()
                    .map(option -> {
                        option.setQuestionId(savedQuestionDTO.getQuestionId());
                        return answerOptionService.createAnswerOption(option);
                    })
                    .collect(Collectors.toList());

            savedQuestionDTO.setOptions(savedOptions);
        }

        return savedQuestionDTO;
    }

    @Transactional
    public QuestionDTO updateQuestion(UUID id, QuestionDTO questionDTO) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + id));

        // Cập nhật thông tin
        if (questionDTO.getQuestionText() != null) {
            question.setQuestionText(questionDTO.getQuestionText());
        }

        if (questionDTO.getImageUrl() != null) {
            question.setImageUrl(questionDTO.getImageUrl());
        }

        if (questionDTO.getAudioUrl() != null) {
            question.setAudioUrl(questionDTO.getAudioUrl());
        }

        // Nếu có yêu cầu thay đổi test
        if (questionDTO.getTestId() != null &&
                !question.getTest().getTestId().toString().equals(questionDTO.getTestId())) {

            Test newTest = testRepository.findById(UUID.fromString(questionDTO.getTestId()))
                    .orElseThrow(() -> new RuntimeException("Test not found with ID: " + questionDTO.getTestId()));

            question.setTest(newTest);
        }

        Question updatedQuestion = questionRepository.save(question);
        QuestionDTO updatedQuestionDTO = convertToDTO(updatedQuestion);

        // Cập nhật các đáp án nếu có
        if (questionDTO.getOptions() != null) {
            // Lấy danh sách đáp án hiện tại
            List<AnswerOptionDTO> currentOptions = answerOptionService.getAnswerOptionsByQuestionId(id);

            // Xóa các đáp án cũ nếu cần thiết
            if (currentOptions != null && !currentOptions.isEmpty()) {
                for (AnswerOptionDTO option : currentOptions) {
                    answerOptionService.deleteAnswerOption(UUID.fromString(option.getOptionId()));
                }
            }

            // Tạo các đáp án mới
            List<AnswerOptionDTO> updatedOptions = questionDTO.getOptions().stream()
                    .map(option -> {
                        option.setQuestionId(updatedQuestionDTO.getQuestionId());
                        return answerOptionService.createAnswerOption(option);
                    })
                    .collect(Collectors.toList());

            updatedQuestionDTO.setOptions(updatedOptions);
        }

        return updatedQuestionDTO;
    }

    @Transactional
    public void deleteQuestion(UUID id) {
        if (!questionRepository.existsById(id)) {
            throw new RuntimeException("Question not found with ID: " + id);
        }

        // Xóa tất cả đáp án của câu hỏi
        answerOptionService.deleteAnswerOptionsByQuestionId(id);

        // Xóa câu hỏi
        questionRepository.deleteById(id);
    }

    @Transactional
    public void deleteQuestionsByTestId(UUID testId) {
        // Lấy danh sách câu hỏi thuộc test
        List<Question> questions = questionRepository.findByTestTestId(testId);

        // Xóa tất cả đáp án của từng câu hỏi
        for (Question question : questions) {
            answerOptionService.deleteAnswerOptionsByQuestionId(question.getQuestionId());
        }

        // Xóa tất cả câu hỏi
        questionRepository.deleteByTestTestId(testId);
    }

    public Long countQuestionsByTestId(UUID testId) {
        return questionRepository.countByTestTestId(testId);
    }

    // Helper method to convert Entity to DTO
    private QuestionDTO convertToDTO(Question question) {
        return new QuestionDTO(
                question.getQuestionId().toString(),
                question.getTest().getTestId().toString(),
                question.getQuestionText(),
                question.getImageUrl(),
                question.getAudioUrl(),
                question.getCorrectAnswerText());
    }
}