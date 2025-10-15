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
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.AnswerOptionDTO;
import com.example.service.AnswerOptionService;

@RestController
@RequestMapping("/api/answer-options")
public class AnswerOptionController {

    private final AnswerOptionService answerOptionService;

    @Autowired
    public AnswerOptionController(AnswerOptionService answerOptionService) {
        this.answerOptionService = answerOptionService;
    }

    // Tạo lựa chọn trả lời mới
    @PostMapping
    public ResponseEntity<AnswerOptionDTO> createAnswerOption(@RequestBody AnswerOptionDTO answerOptionDTO) {
        AnswerOptionDTO createdAnswerOption = answerOptionService.createAnswerOption(answerOptionDTO);
        return new ResponseEntity<>(createdAnswerOption, HttpStatus.CREATED);
    }

    // Lấy lựa chọn trả lời theo ID
    @GetMapping("/{id}")
    public ResponseEntity<AnswerOptionDTO> getAnswerOptionById(@PathVariable UUID id) {
        AnswerOptionDTO answerOptionDTO = answerOptionService.getAnswerOptionById(id);
        return ResponseEntity.ok(answerOptionDTO);
    }

    // Cập nhật lựa chọn trả lời
    @PutMapping("/{id}")
    public ResponseEntity<AnswerOptionDTO> updateAnswerOption(@PathVariable UUID id, @RequestBody AnswerOptionDTO answerOptionDTO) {
        AnswerOptionDTO updatedAnswerOption = answerOptionService.updateAnswerOption(id, answerOptionDTO);
        return ResponseEntity.ok(updatedAnswerOption);
    }

    // Xóa lựa chọn trả lời
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnswerOption(@PathVariable UUID id) {
        answerOptionService.deleteAnswerOption(id);
        return ResponseEntity.noContent().build();
    }

    // Lấy tất cả lựa chọn trả lời
    @GetMapping
    public ResponseEntity<List<AnswerOptionDTO>> getAllAnswerOptions() {
        List<AnswerOptionDTO> answerOptionsDTO = answerOptionService.getAllAnswerOptions();
        return ResponseEntity.ok(answerOptionsDTO);
    }
}
