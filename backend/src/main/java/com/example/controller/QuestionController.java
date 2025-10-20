package com.example.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.QuestionService;
import com.example.dto.QuestionDTO;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    // Khởi tạo câu hỏi:
    @PostMapping
    public ResponseEntity<QuestionDTO> createQuestion(@RequestBody QuestionDTO questionDTO) {
        QuestionDTO createdQuestion = questionService.createQuestion(questionDTO);
        return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
    }

    // Lấy thông tin câu hỏi:
    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionDTO> getQuestionByID(@PathVariable UUID questionId) {
        QuestionDTO questionDTO = questionService.getQuestionById(questionId);
        return new ResponseEntity<>(questionDTO, HttpStatus.OK);
    }
    
    @GetMapping("/test/{testId}")
    public ResponseEntity<?> getQuestionsByTestId(@PathVariable UUID testId) {
        return new ResponseEntity<>(questionService.getQuestionsByTestId(testId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<QuestionDTO>> searchQuestions(@RequestParam String keyword) {
        List<QuestionDTO> results = questionService.searchQuestionsByText(keyword);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    // Chỉnh sửa câu hỏi:
    @PutMapping("/{questionId}")
    public ResponseEntity<QuestionDTO> updateQuestion(@RequestBody QuestionDTO questionDTO, @PathVariable UUID questionId) {
        QuestionDTO updatedQuestion = questionService.updateQuestion(questionId, questionDTO);
        return new ResponseEntity<>(updatedQuestion, HttpStatus.OK);
    }

    // Xoá câu hỏi:
    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestionByID(@PathVariable UUID questionId) {
        questionService.deleteQuestion(questionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/test/{testId}")
    public ResponseEntity<Void> deleteQuestionByTestID(@PathVariable UUID testId) {
        questionService.deleteQuestionsByTestId(testId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
