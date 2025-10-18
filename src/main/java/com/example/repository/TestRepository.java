package com.example.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.entity.Test;
import com.example.entity.Test.TestType;
import com.example.entity.Topic;

@Repository
public interface TestRepository extends JpaRepository<Test, UUID> {
    @Override
    Optional<Test> findById(UUID id);
    
    List<Test> findByTopic(Topic topic);
    
    List<Test> findByTopicTopicId(UUID topicId);
    
    Page<Test> findByTopicTopicId(UUID topicId, Pageable pageable);
    
    List<Test> findByTestType(TestType testType);
    
    Page<Test> findByTestType(TestType testType, Pageable pageable);
    
    List<Test> findByTopicTopicIdAndTestType(UUID topicId, TestType testType);
    
    Page<Test> findByTopicTopicIdAndTestType(UUID topicId, TestType testType, Pageable pageable);
    
    List<Test> findByNumQuestionsGreaterThanEqual(Integer minQuestions);
    
    Long countByTopicTopicId(UUID topicId);
    
    @Query("SELECT t FROM Test t ORDER BY t.createdAt DESC")
    List<Test> findRecentTests(Pageable pageable);
    
    void deleteByTopicTopicId(UUID topicId);
}