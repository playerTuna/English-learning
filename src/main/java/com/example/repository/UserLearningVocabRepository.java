package com.example.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.UserLearningVocab;
import com.example.entity.UserLearningVocab.UserLearningVocabId;

@Repository
public interface UserLearningVocabRepository extends JpaRepository<UserLearningVocab, UserLearningVocabId> {

        // Tìm kiếm bằng khóa chính tổng hợp
        @Override
        Optional<UserLearningVocab> findById(UserLearningVocabId id);

        // Tìm kiếm theo userId và vocabId
        Optional<UserLearningVocab> findByUserUserIdAndVocabularyVocabId(UUID userId, UUID vocabId);

        // Kiểm tra tồn tại theo userId và vocabId
        boolean existsByUserUserIdAndVocabularyVocabId(UUID userId, UUID vocabId);

        // Tìm tất cả từ vựng mà user đang học
        List<UserLearningVocab> findByUserUserId(UUID userId);

        // Tìm tất cả từ vựng mà user đang học với phân trang
        Page<UserLearningVocab> findByUserUserId(UUID userId, Pageable pageable);

        // Tìm từ vựng theo topic mà user đang học
        @Query("SELECT ulv FROM UserLearningVocab ulv WHERE ulv.user.userId = :userId AND ulv.vocabulary.topic.topicId = :topicId")
        List<UserLearningVocab> findByUserIdAndTopicId(@Param("userId") UUID userId, @Param("topicId") UUID topicId);

        // Tìm từ vựng theo topic mà user đang học với phân trang
        @Query("SELECT ulv FROM UserLearningVocab ulv WHERE ulv.user.userId = :userId AND ulv.vocabulary.topic.topicId = :topicId")
        Page<UserLearningVocab> findByUserIdAndTopicId(@Param("userId") UUID userId, @Param("topicId") UUID topicId,
                        Pageable pageable);

        // Tìm từ vựng cần ôn tập (nextReviewAt <= now)
        @Query("SELECT ulv FROM UserLearningVocab ulv WHERE ulv.user.userId = :userId AND ulv.nextReviewAt <= :now")
        List<UserLearningVocab> findVocabsToReview(@Param("userId") UUID userId, @Param("now") OffsetDateTime now);

        // Tìm từ vựng cần ôn tập (nextReviewAt <= now) với phân trang
        @Query("SELECT ulv FROM UserLearningVocab ulv WHERE ulv.user.userId = :userId AND ulv.nextReviewAt <= :now")
        Page<UserLearningVocab> findVocabsToReview(@Param("userId") UUID userId, @Param("now") OffsetDateTime now,
                        Pageable pageable);

        // Tìm từ vựng với tỷ lệ thành công thấp
        @Query("SELECT ulv FROM UserLearningVocab ulv WHERE ulv.user.userId = :userId AND ulv.successRate <= :rate")
        List<UserLearningVocab> findVocabsWithLowSuccessRate(@Param("userId") UUID userId, @Param("rate") Float rate);

        // Tìm từ vựng mới được thêm vào gần đây
        @Query("SELECT ulv FROM UserLearningVocab ulv WHERE ulv.user.userId = :userId AND ulv.createdAt >= :since")
        List<UserLearningVocab> findRecentlyAddedVocabs(@Param("userId") UUID userId,
                        @Param("since") OffsetDateTime since);

        // Xóa tất cả từ vựng của một user
        void deleteByUserUserId(UUID userId);

        // Xóa từ vựng cụ thể của một user
        void deleteByUserUserIdAndVocabularyVocabId(UUID userId, UUID vocabId);
}
