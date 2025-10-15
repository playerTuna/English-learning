package com.example.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.AnswerOptionDTO;
import com.example.entity.AnswerOption;
import com.example.entity.Question;
import com.example.repository.AnswerOptionRepository;
import com.example.repository.QuestionRepository;

@Service
public class AnswerOptionService {

    private final AnswerOptionRepository answerOptionRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public AnswerOptionService(AnswerOptionRepository answerOptionRepository,
            QuestionRepository questionRepository) {
        this.answerOptionRepository = answerOptionRepository;
        this.questionRepository = questionRepository;
    }

    public List<AnswerOptionDTO> getAllAnswerOptions() {
        return answerOptionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AnswerOptionDTO getAnswerOptionById(UUID id) {
        AnswerOption answerOption = answerOptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer option not found with ID: " + id));
        return convertToDTO(answerOption);
    }

    public List<AnswerOptionDTO> getAnswerOptionsByQuestionId(UUID questionId) {
        return answerOptionRepository.findByQuestionQuestionId(questionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AnswerOptionDTO> getCorrectAnswersByQuestionId(UUID questionId) {
        return answerOptionRepository.findCorrectAnswersByQuestionId(questionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AnswerOptionDTO createAnswerOption(AnswerOptionDTO answerOptionDTO) {
        // Kiểm tra questionId hợp lệ
        Question question = questionRepository.findById(UUID.fromString(answerOptionDTO.getQuestionId()))
                .orElseThrow(
                        () -> new RuntimeException("Question not found with ID: " + answerOptionDTO.getQuestionId()));

        // Tạo AnswerOption mới
        AnswerOption answerOption = new AnswerOption();
        answerOption.setQuestion(question);
        answerOption.setOptionText(answerOptionDTO.getOptionText());
        answerOption.setCorrect(answerOptionDTO.getIsCorrect());

        AnswerOption savedAnswerOption = answerOptionRepository.save(answerOption);
        return convertToDTO(savedAnswerOption);
    }

    @Transactional
    public AnswerOptionDTO updateAnswerOption(UUID id, AnswerOptionDTO answerOptionDTO) {
        AnswerOption answerOption = answerOptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer option not found with ID: " + id));

        // Cập nhật thông tin
        if (answerOptionDTO.getOptionText() != null) {
            answerOption.setOptionText(answerOptionDTO.getOptionText());
        }

        if (answerOptionDTO.getIsCorrect() != null) {
            answerOption.setCorrect(answerOptionDTO.getIsCorrect());
        }

        // Nếu có yêu cầu thay đổi question
        if (answerOptionDTO.getQuestionId() != null &&
                !answerOption.getQuestion().getQuestionId().toString().equals(answerOptionDTO.getQuestionId())) {

            Question newQuestion = questionRepository.findById(UUID.fromString(answerOptionDTO.getQuestionId()))
                    .orElseThrow(() -> new RuntimeException(
                            "Question not found with ID: " + answerOptionDTO.getQuestionId()));

            answerOption.setQuestion(newQuestion);
        }

        AnswerOption updatedAnswerOption = answerOptionRepository.save(answerOption);
        return convertToDTO(updatedAnswerOption);
    }

    @Transactional
    public void deleteAnswerOption(UUID id) {
        if (!answerOptionRepository.existsById(id)) {
            throw new RuntimeException("Answer option not found with ID: " + id);
        }
        answerOptionRepository.deleteById(id);
    }

    @Transactional
    public void deleteAnswerOptionsByQuestionId(UUID questionId) {
        answerOptionRepository.deleteByQuestionQuestionId(questionId);
    }

    public Long countAnswerOptionsByQuestionId(UUID questionId) {
        return answerOptionRepository.countByQuestionQuestionId(questionId);
    }

    // Helper method to convert Entity to DTO
    private AnswerOptionDTO convertToDTO(AnswerOption answerOption) {
        return new AnswerOptionDTO(
                answerOption.getOptionId().toString(),
                answerOption.getQuestion().getQuestionId().toString(),
                answerOption.getOptionText(),
                answerOption.isCorrect());
    }
}