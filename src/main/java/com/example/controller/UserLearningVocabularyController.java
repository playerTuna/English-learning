package com.example.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.UserLearningVocabDTO;
import com.example.entity.UserLearningVocab;
import com.example.service.UserLearningVocabService;

@RestController
@RequestMapping("/api/user-vocabulary")
public class UserLearningVocabularyController {

    private final UserLearningVocabService userLearningVocabService;

    @Autowired
    public UserLearningVocabularyController(UserLearningVocabService userLearningVocabService) {
        this.userLearningVocabService = userLearningVocabService;
    }

    /**
     * Thêm từ vựng vào danh sách học của người dùng
     */
    @PostMapping("/{userId}/add/{vocabId}")
    public ResponseEntity<UserLearningVocabDTO> addVocabToUser(
            @PathVariable("userId") UUID userId,
            @PathVariable("vocabId") UUID vocabId) {
        
        UserLearningVocab userLearningVocab = userLearningVocabService.addVocabToUser(userId, vocabId);
        return new ResponseEntity<>(convertToDTO(userLearningVocab), HttpStatus.CREATED);
    }

    /**
     * Lấy tất cả từ vựng đang học của người dùng
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<UserLearningVocabDTO>> getUserVocabularies(
            @PathVariable("userId") UUID userId) {
        
        List<UserLearningVocab> userLearningVocabs = userLearningVocabService.getUserVocabularies(userId);
        List<UserLearningVocabDTO> dtos = userLearningVocabs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Lấy tất cả từ vựng đang học của người dùng với phân trang
     */
    @GetMapping("/{userId}/paginated")
    public ResponseEntity<Page<UserLearningVocabDTO>> getUserVocabulariesPaginated(
            @PathVariable("userId") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastReviewAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<UserLearningVocab> userLearningVocabPage = userLearningVocabService.getUserVocabulariesPaginated(userId, pageable);
        
        // Chuyển đổi Page<UserLearningVocab> thành Page<UserLearningVocabDTO>
        Page<UserLearningVocabDTO> dtoPage = userLearningVocabPage.map(this::convertToDTO);
        
        return ResponseEntity.ok(dtoPage);
    }

    /**
     * Lấy các từ vựng cần ôn tập
     */
    @GetMapping("/{userId}/review")
    public ResponseEntity<List<UserLearningVocabDTO>> getVocabsToReview(
            @PathVariable("userId") UUID userId) {
        
        List<UserLearningVocab> userLearningVocabs = userLearningVocabService.getVocabsToReview(userId);
        List<UserLearningVocabDTO> dtos = userLearningVocabs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Cập nhật thông tin học từ vựng sau khi ôn tập
     */
    @PostMapping("/{userId}/review/{vocabId}")
    public ResponseEntity<UserLearningVocabDTO> updateAfterReview(
            @PathVariable("userId") UUID userId,
            @PathVariable("vocabId") UUID vocabId,
            @RequestBody Map<String, Boolean> payload) {
        
        // Lấy kết quả trả lời từ request body
        Boolean isCorrect = payload.get("isCorrect");
        if (isCorrect == null) {
            return ResponseEntity.badRequest().build();
        }
        
        UserLearningVocab updated = userLearningVocabService.updateAfterReview(userId, vocabId, isCorrect);
        return ResponseEntity.ok(convertToDTO(updated));
    }

    /**
     * Xóa từ vựng khỏi danh sách học của người dùng
     */
    @DeleteMapping("/{userId}/remove/{vocabId}")
    public ResponseEntity<Void> removeVocabFromUser(
            @PathVariable("userId") UUID userId,
            @PathVariable("vocabId") UUID vocabId) {
        
        userLearningVocabService.removeVocabFromUser(userId, vocabId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lấy từ vựng theo chủ đề mà người dùng đang học
     */
    @GetMapping("/{userId}/topic/{topicId}")
    public ResponseEntity<List<UserLearningVocabDTO>> getUserVocabsByTopic(
            @PathVariable("userId") UUID userId,
            @PathVariable("topicId") UUID topicId) {
        
        List<UserLearningVocab> userLearningVocabs = userLearningVocabService.getUserVocabsByTopic(userId, topicId);
        List<UserLearningVocabDTO> dtos = userLearningVocabs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Lấy từ vựng có tỷ lệ thành công thấp
     */
    @GetMapping("/{userId}/difficult")
    public ResponseEntity<List<UserLearningVocabDTO>> getLowSuccessRateVocabs(
            @PathVariable("userId") UUID userId,
            @RequestParam(defaultValue = "0.5") float maxSuccessRate) {
        
        List<UserLearningVocab> userLearningVocabs = userLearningVocabService.getLowSuccessRateVocabs(userId, maxSuccessRate);
        List<UserLearningVocabDTO> dtos = userLearningVocabs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Helper method để chuyển đổi từ entity sang DTO
     */
    private UserLearningVocabDTO convertToDTO(UserLearningVocab userLearningVocab) {
        return new UserLearningVocabDTO(
                userLearningVocab.getUser().getUserId().toString(),
                userLearningVocab.getUser().getUsername(),
                userLearningVocab.getVocabulary().getVocabId().toString(),
                userLearningVocab.getVocabulary().getWord(),
                userLearningVocab.getVocabulary().getMeaning(),
                userLearningVocab.getVocabulary().getWordType().toString(),
                userLearningVocab.getVocabulary().getTopic() != null ? 
                        userLearningVocab.getVocabulary().getTopic().getName() : "",
                userLearningVocab.getLastReviewAt(),
                userLearningVocab.getNextReviewAt(),
                userLearningVocab.getEaseFactor(),
                userLearningVocab.getRepetitionCount(),
                userLearningVocab.getSuccessRate()
        );
    }
}
