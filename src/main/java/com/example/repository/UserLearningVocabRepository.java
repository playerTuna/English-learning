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

        @Override
        Optional<UserLearningVocab> findById(UserLearningVocabId id);

        Optional<UserLearningVocab> findByUserUserIdAndVocabularyVocabId(UUID userId, UUID vocabId);

        boolean existsByUserUserIdAndVocabularyVocabId(UUID userId, UUID vocabId);

        List<UserLearningVocab> findByUserUserId(UUID userId);

        Page<UserLearningVocab> findByUserUserId(UUID userId, Pageable pageable);

        @Query("SELECT ulv FROM UserLearningVocab ulv WHERE ulv.user.userId = :userId AND ulv.vocabulary.topic.topicId = :topicId")
        List<UserLearningVocab> findByUserIdAndTopicId(@Param("userId") UUID userId, @Param("topicId") UUID topicId);

        @Query("SELECT ulv FROM UserLearningVocab ulv WHERE ulv.user.userId = :userId AND ulv.vocabulary.topic.topicId = :topicId")
        Page<UserLearningVocab> findByUserIdAndTopicId(@Param("userId") UUID userId, @Param("topicId") UUID topicId,
                        Pageable pageable);

        @Query("SELECT ulv FROM UserLearningVocab ulv WHERE ulv.user.userId = :userId AND ulv.nextReviewAt <= :now")
        List<UserLearningVocab> findVocabsToReview(@Param("userId") UUID userId, @Param("now") OffsetDateTime now);

        @Query("SELECT ulv FROM UserLearningVocab ulv WHERE ulv.user.userId = :userId AND ulv.nextReviewAt <= :now")
        Page<UserLearningVocab> findVocabsToReview(@Param("userId") UUID userId, @Param("now") OffsetDateTime now,
                        Pageable pageable);

        @Query("SELECT ulv FROM UserLearningVocab ulv WHERE ulv.user.userId = :userId AND ulv.successRate <= :rate")
        List<UserLearningVocab> findVocabsWithLowSuccessRate(@Param("userId") UUID userId, @Param("rate") Float rate);

        @Query("SELECT ulv FROM UserLearningVocab ulv WHERE ulv.user.userId = :userId AND ulv.createdAt >= :since")
        List<UserLearningVocab> findRecentlyAddedVocabs(@Param("userId") UUID userId,
                        @Param("since") OffsetDateTime since);

        void deleteByUserUserId(UUID userId);

        void deleteByUserUserIdAndVocabularyVocabId(UUID userId, UUID vocabId);
}
