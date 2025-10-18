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

import com.example.entity.Test;
import com.example.entity.TestResult;
import com.example.entity.User;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, UUID> {
    @Override
    Optional<TestResult> findById(UUID id);
    
    List<TestResult> findByUser(User user);
    
    List<TestResult> findByUserUserId(UUID userId);
    
    Page<TestResult> findByUserUserId(UUID userId, Pageable pageable);
    
    List<TestResult> findByTest(Test test);
    
    List<TestResult> findByTestTestId(UUID testId);
    
    Page<TestResult> findByTestTestId(UUID testId, Pageable pageable);
    
    List<TestResult> findByUserUserIdAndTestTestId(UUID userId, UUID testId);

    @Query("SELECT tr FROM TestResult tr WHERE tr.user.userId = :userId ORDER BY tr.takenAt DESC")
    List<TestResult> findRecentTestResultsByUserId(@Param("userId") UUID userId, Pageable pageable);
    
    @Query("SELECT tr FROM TestResult tr WHERE tr.user.userId = :userId AND tr.takenAt BETWEEN :startDate AND :endDate")
    List<TestResult> findTestResultsByUserIdAndDateRange(
            @Param("userId") UUID userId, 
            @Param("startDate") OffsetDateTime startDate, 
            @Param("endDate") OffsetDateTime endDate);
    
    @Query("SELECT AVG(tr.score) FROM TestResult tr WHERE tr.user.userId = :userId")
    Double calculateAverageScoreByUserId(@Param("userId") UUID userId);
    
    @Query("SELECT AVG(tr.score) FROM TestResult tr WHERE tr.user.userId = :userId AND tr.test.testId = :testId")
    Double calculateAverageScoreByUserIdAndTestId(@Param("userId") UUID userId, @Param("testId") UUID testId);
    
    @Query("SELECT MAX(tr.score) FROM TestResult tr WHERE tr.user.userId = :userId AND tr.test.testId = :testId")
    Integer findHighestScoreByUserIdAndTestId(@Param("userId") UUID userId, @Param("testId") UUID testId);
    
    void deleteByUserUserId(UUID userId);
    
    void deleteByTestTestId(UUID testId);
}