package com.example.service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.User;
import com.example.entity.UserLearningVocab;
import com.example.entity.Vocabulary;
import com.example.repository.UserLearningVocabRepository;
import com.example.repository.UserRepository;
import com.example.repository.VocabularyRepository;

@Service
public class UserLearningVocabService {

    private final UserLearningVocabRepository userLearningVocabRepository;
    private final UserRepository userRepository;
    private final VocabularyRepository vocabularyRepository;

    @Autowired
    public UserLearningVocabService(
            UserLearningVocabRepository userLearningVocabRepository,
            UserRepository userRepository,
            VocabularyRepository vocabularyRepository) {
        this.userLearningVocabRepository = userLearningVocabRepository;
        this.userRepository = userRepository;
        this.vocabularyRepository = vocabularyRepository;
    }

    /**
     * Thêm từ vựng vào danh sách học của người dùng
     */
    @Transactional
    public UserLearningVocab addVocabToUser(UUID userId, UUID vocabId) {
        // Kiểm tra người dùng và từ vựng có tồn tại không
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Vocabulary vocabulary = vocabularyRepository.findById(vocabId)
                .orElseThrow(() -> new RuntimeException("Vocabulary not found with id: " + vocabId));

        // Kiểm tra xem người dùng đã học từ này chưa
        Optional<UserLearningVocab> existingEntry = userLearningVocabRepository
                .findByUserUserIdAndVocabularyVocabId(userId, vocabId);

        if (existingEntry.isPresent()) {
            return existingEntry.get();
        }

        // Tạo mối quan hệ mới
        UserLearningVocab userLearningVocab = new UserLearningVocab(user, vocabulary);

        // Thiết lập thời gian ôn tập tiếp theo (ví dụ: 1 ngày sau)
        userLearningVocab.setNextReviewAt(OffsetDateTime.now().plus(1, ChronoUnit.DAYS));

        return userLearningVocabRepository.save(userLearningVocab);
    }

    /**
     * Lấy tất cả từ vựng đang học của người dùng
     */
    public List<UserLearningVocab> getUserVocabularies(UUID userId) {
        // Kiểm tra người dùng có tồn tại không
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        return userLearningVocabRepository.findByUserUserId(userId);
    }

    /**
     * Lấy tất cả từ vựng đang học của người dùng với phân trang
     */
    public Page<UserLearningVocab> getUserVocabulariesPaginated(UUID userId, Pageable pageable) {
        // Kiểm tra người dùng có tồn tại không
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        return userLearningVocabRepository.findByUserUserId(userId, pageable);
    }

    /**
     * Lấy các từ vựng cần ôn tập
     */
    public List<UserLearningVocab> getVocabsToReview(UUID userId) {
        // Kiểm tra người dùng có tồn tại không
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        return userLearningVocabRepository.findVocabsToReview(userId, OffsetDateTime.now());
    }

    /**
     * Cập nhật thông tin học từ vựng sau khi ôn tập
     */
    @Transactional
    public UserLearningVocab updateAfterReview(UUID userId, UUID vocabId, boolean isCorrect) {
        // Tìm mối quan hệ
        UserLearningVocab userLearningVocab = userLearningVocabRepository
                .findByUserUserIdAndVocabularyVocabId(userId, vocabId)
                .orElseThrow(() -> new RuntimeException("Learning relationship not found"));

        // Cập nhật thông tin
        userLearningVocab.setLastReviewAt(OffsetDateTime.now());
        userLearningVocab.setRepetitionCount(userLearningVocab.getRepetitionCount() + 1);

        float oldSuccessRate = userLearningVocab.getSuccessRate();
        long repetitionCount = userLearningVocab.getRepetitionCount();
        float newSuccessRate = (oldSuccessRate * (repetitionCount - 1) + (isCorrect ? 1.0f : 0.0f)) / repetitionCount;
        userLearningVocab.setSuccessRate(newSuccessRate);

        // Điều chỉnh hệ số dễ dàng dựa trên kết quả
        float easeFactor = userLearningVocab.getEaseFactor();
        if (isCorrect) {
            // Tăng hệ số nếu trả lời đúng
            easeFactor = Math.min(easeFactor + 0.1f, 3.0f);
        } else {
            // Giảm hệ số nếu trả lời sai
            easeFactor = Math.max(easeFactor - 0.2f, 1.3f);
        }
        userLearningVocab.setEaseFactor(easeFactor);

        // Tính toán thời gian ôn tập tiếp theo dựa trên hệ số
        int daysUntilNextReview;
        if (repetitionCount <= 1) {
            daysUntilNextReview = 1;
        } else if (repetitionCount == 2) {
            daysUntilNextReview = 3;
        } else {
            daysUntilNextReview = (int) Math.round(userLearningVocab.getEaseFactor() *
                    ChronoUnit.DAYS.between(userLearningVocab.getLastReviewAt(),
                            userLearningVocab.getNextReviewAt()));
            daysUntilNextReview = Math.max(1, daysUntilNextReview);
        }

        // Nếu trả lời sai, ôn tập sớm hơn
        if (!isCorrect) {
            daysUntilNextReview = Math.max(1, daysUntilNextReview / 2);
        }

        userLearningVocab.setNextReviewAt(OffsetDateTime.now().plus(daysUntilNextReview, ChronoUnit.DAYS));

        return userLearningVocabRepository.save(userLearningVocab);
    }

    /**
     * Xóa từ vựng khỏi danh sách học của người dùng
     */
    @Transactional
    public void removeVocabFromUser(UUID userId, UUID vocabId) {
        // Kiểm tra mối quan hệ có tồn tại không
        if (!userLearningVocabRepository.existsByUserUserIdAndVocabularyVocabId(userId, vocabId)) {
            throw new RuntimeException("Learning relationship not found");
        }

        userLearningVocabRepository.deleteByUserUserIdAndVocabularyVocabId(userId, vocabId);
    }

    /**
     * Lấy từ vựng theo chủ đề mà người dùng đang học
     */
    public List<UserLearningVocab> getUserVocabsByTopic(UUID userId, UUID topicId) {
        // Kiểm tra người dùng có tồn tại không
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        return userLearningVocabRepository.findByUserIdAndTopicId(userId, topicId);
    }

    /**
     * Lấy từ vựng có tỷ lệ thành công thấp
     */
    public List<UserLearningVocab> getLowSuccessRateVocabs(UUID userId, float maxSuccessRate) {
        // Kiểm tra người dùng có tồn tại không
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        return userLearningVocabRepository.findVocabsWithLowSuccessRate(userId, maxSuccessRate);
    }
}