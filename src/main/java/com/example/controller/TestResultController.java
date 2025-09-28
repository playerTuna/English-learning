package com.example.controller;

import java.time.OffsetDateTime;
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

import com.example.dto.TestResultDTO;
import com.example.service.TestResultService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/test-results")
public class TestResultController {

    private final TestResultService testResultService;

    @Autowired
    public TestResultController(TestResultService testResultService) {
        this.testResultService = testResultService;
    }

    @PostMapping
    public ResponseEntity<TestResultDTO> createTestResult(@Valid @RequestBody TestResultDTO testResultDTO) {
        TestResultDTO result = testResultService.createTestResult(testResultDTO);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestResultDTO> getTestResultById(@PathVariable UUID id) {
        TestResultDTO result = testResultService.getTestResultById(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/{userId}/test/{testId}")
    public ResponseEntity<List<TestResultDTO>> getTestResultByUserIdAndTestId(@PathVariable UUID userId, @PathVariable UUID testId) {
        List<TestResultDTO> results = testResultService.getTestResultsByUserIdAndTestId(userId, testId);
        return ResponseEntity.ok(results);
    }

    @GetMapping
    public ResponseEntity<List<TestResultDTO>> getAllTestResults() {
        List<TestResultDTO> results = testResultService.getAllTestResults();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TestResultDTO>> getTestResultsByUserId(@PathVariable UUID userId) {
        List<TestResultDTO> results = testResultService.getTestResultsByUserId(userId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/test/{testId}")
    public ResponseEntity<List<TestResultDTO>> getTestResultsByTestId(@PathVariable UUID testId) {
        List<TestResultDTO> results = testResultService.getTestResultsByTestId(testId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<TestResultDTO>> getRecentTestResults(@PathVariable UUID userId, @RequestParam(defaultValue = "5") int limit) {
        List<TestResultDTO> results = testResultService.getRecentTestResultsByUserId(userId, limit);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<TestResultDTO>> getTestResultsByDateRange(
            @PathVariable UUID userId,
            @RequestParam OffsetDateTime startDate,
            @RequestParam OffsetDateTime endDate) {
        List<TestResultDTO> results = testResultService.getTestResultsByUserIdAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/user/{userId}/average")
    public ResponseEntity<Double> getAverageScore(@PathVariable UUID userId) {
        Double average = testResultService.calculateAverageScoreByUserId(userId);
        return ResponseEntity.ok(average);
    }

    @GetMapping("/user/{userId}/test/{testId}/average")
    public ResponseEntity<Double> getAverageScoreByTest(@PathVariable UUID userId, @PathVariable UUID testId) {
        Double average = testResultService.calculateAverageScoreByUserIdAndTestId(userId, testId);
        return ResponseEntity.ok(average);
    }

    @GetMapping("/user/{userId}/test/{testId}/highest")
    public ResponseEntity<Integer> getHighestScore(@PathVariable UUID userId, @PathVariable UUID testId) {
        Integer highest = testResultService.findHighestScoreByUserIdAndTestId(userId, testId);
        return ResponseEntity.ok(highest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestResultDTO> updateTestResult(@PathVariable UUID id, @Valid @RequestBody TestResultDTO testResultDTO) {
        TestResultDTO updated = testResultService.updateTestResult(id, testResultDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestResult(@PathVariable UUID id) {
        testResultService.deleteTestResult(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}")
    public void deleteTestResultsByUserId(@PathVariable UUID userId) {
        testResultService.deleteTestResultsByUserId(userId);
    }

    @DeleteMapping("/test/{testId}")
    public void deleteTestResultsByTestId(@PathVariable UUID testId) {
        testResultService.deleteTestResultsByTestId(testId);
    }
}