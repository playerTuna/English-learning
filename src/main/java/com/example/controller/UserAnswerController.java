package com.example.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.UserAnswerDTO;
import com.example.service.UserAnswerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user_answers")
public class UserAnswerController {

    private final UserAnswerService userAnswerService;

    @Autowired
    public UserAnswerController(UserAnswerService userAnswerService) {
        this.userAnswerService = userAnswerService;
    }

    @PostMapping
    public ResponseEntity<UserAnswerDTO> createUserAnswer(@Valid @RequestBody UserAnswerDTO userAnswerDTO) {
        UserAnswerDTO newUserAnswer = userAnswerService.createUserAnswer(userAnswerDTO);
        return ResponseEntity.ok(newUserAnswer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAnswerDTO> getUserAnswerById(@PathVariable UUID id) {
        UserAnswerDTO userAnswer = userAnswerService.getUserAnswerById(id);
        return ResponseEntity.ok(userAnswer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAnswerDTO> updateUserAnswer(
            @PathVariable UUID id,
            @Valid @RequestBody UserAnswerDTO userAnswerDTO) {
        UserAnswerDTO updatedUserAnswer = userAnswerService.updateUserAnswer(id, userAnswerDTO);
        return ResponseEntity.ok(updatedUserAnswer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserAnswer(@PathVariable UUID id) {
        userAnswerService.deleteUserAnswer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserAnswerDTO>> getUserAnswersByUserId(@PathVariable UUID userId) {
        List<UserAnswerDTO> userAnswers = userAnswerService.getUserAnswersByUserId(userId);
        return ResponseEntity.ok(userAnswers);
    }

    @GetMapping("/test/{testId}")
    public ResponseEntity<List<UserAnswerDTO>> getUserAnswersByTestId(@PathVariable UUID testId) {
        List<UserAnswerDTO> userAnswers = userAnswerService.getUserAnswersByTestId(testId);
        return ResponseEntity.ok(userAnswers);
    }

    @GetMapping("/user/{userId}/test/{testId}")
    public ResponseEntity<List<UserAnswerDTO>> getUserAnswersByUserIdAndTestId(
            @PathVariable UUID userId,
            @PathVariable UUID testId) {
        List<UserAnswerDTO> userAnswers = userAnswerService.getUserAnswersByUserIdAndTestId(userId, testId);
        return ResponseEntity.ok(userAnswers);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteUserAnswersByUserId(@PathVariable UUID userId) {
        userAnswerService.deleteUserAnswersByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/test/{testId}")
    public ResponseEntity<Void> deleteUserAnswersByTestId(@PathVariable UUID testId) {
        userAnswerService.deleteUserAnswersByTestId(testId);
        return ResponseEntity.noContent().build();
    }
}
