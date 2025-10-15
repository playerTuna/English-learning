package com.example.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Test;
import com.example.entity.Test.TestType;
import com.example.entity.Topic;
import com.example.repository.QuestionRepository;
import com.example.repository.TestRepository;
import com.example.repository.TopicRepository;

@Service
public class TestService {
    
    private final TestRepository testRepository;
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    
    @Autowired
    public TestService(
            TestRepository testRepository,
            TopicRepository topicRepository,
            QuestionRepository questionRepository) {
        this.testRepository = testRepository;
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
    }
    
    /**
     * Tạo bài test mới
     */
    @Transactional
    public Test createTest(Test test) {
        // Validate
        if (test.getNumQuestions() == null || test.getNumQuestions() <= 0) {
            throw new IllegalArgumentException("Number of Questions must be greater than 0");
        }
        // Kiểm tra topic có tồn tại không
        if (test.getTopic() != null && test.getTopic().getTopicId() != null) {
            Topic topic = topicRepository.findById(test.getTopic().getTopicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
            test.setTopic(topic);
        }
        // Thiết lập thời gian tạo
        test.setCreatedAt(OffsetDateTime.now());
        return testRepository.save(test);
    }
    
    /**
     * Lấy bài test theo ID
     */
    public Test getTestById(UUID id) {
        return testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test not found with id: " + id));
    }
    
    /**
     * Cập nhật bài test
     */
    @Transactional
    public Test updateTest(UUID id, Test testDetails) {
        Test test = getTestById(id);
        // Cập nhật thông tin
        if (testDetails.getTestType() != null) {
            test.setTestType(testDetails.getTestType());
        }
        if (testDetails.getNumQuestions() != null) {
            if (testDetails.getNumQuestions() <= 0) {
                throw new IllegalArgumentException("Number of Questionmust be greater than 0");
            }
            test.setNumQuestions(testDetails.getNumQuestions());
        }
        if (testDetails.getTopic() != null && testDetails.getTopic().getTopicId() != null) {
            Topic topic = topicRepository.findById(testDetails.getTopic().getTopicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
            test.setTopic(topic);
        }
        // Cập nhật thời gian
        test.setUpdatedAt(OffsetDateTime.now());
        return testRepository.save(test);
    }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
    
    /**
     * Xóa bài test
     */
    @Transactional
    public void deleteTest(UUID id) {
        // Kiểm tra bài test có tồn tại không
        if (!testRepository.existsById(id)) {
            throw new RuntimeException("Test not found with id: " + id);
        }
        
        // Xóa các câu hỏi của bài test trước
        questionRepository.deleteByTestTestId(id);
        
        // Xóa bài test
        testRepository.deleteById(id);
    }
    
    /**
     * Lấy tất cả bài test
     */
    public List<Test> getAllTests() {
        return testRepository.findAll();
    }
    
    /**
     * Lấy bài test theo chủ đề
     */
    public List<Test> getTestsByTopic(UUID topicId) {
        return testRepository.findByTopicTopicId(topicId);
    }
    
    /**
     * Lấy bài test theo loại
     */
    public List<Test> getTestsByType(TestType testType) {
        return testRepository.findByTestType(testType);
    }
    
    /**
     * Lấy tất cả bài test với phân trang
     */
    public Page<Test> getAllTestsPaginated(Pageable pageable) {
        return testRepository.findAll(pageable);
    }
    
    /**
     * Lấy bài test theo chủ đề với phân trang
     */
    public Page<Test> getTestsByTopicPaginated(UUID topicId, Pageable pageable) {
        return testRepository.findByTopicTopicId(topicId, pageable);
    }
    
    /**
     * Lấy bài test theo loại với phân trang
     */
    public Page<Test> getTestsByTypePaginated(TestType testType, Pageable pageable) {
        return testRepository.findByTestType(testType, pageable);
    }
    
    /**
     * Lấy bài test theo chủ đề và loại với phân trang
     */
    public Page<Test> getTestsByTopicAndTypePaginated(UUID topicId, TestType testType, Pageable pageable) {
        return testRepository.findByTopicTopicIdAndTestType(topicId, testType, pageable);
    }
    
    /**
     * Đếm số lượng bài test theo chủ đề
     */
    public Long countTestsByTopic(UUID topicId) {
        return testRepository.countByTopicTopicId(topicId);
    }
    
    /**
     * Lấy các bài test mới nhất
     */
    public List<Test> getRecentTests(int limit) {
        return testRepository.findRecentTests(Pageable.ofSize(limit));
    }
}