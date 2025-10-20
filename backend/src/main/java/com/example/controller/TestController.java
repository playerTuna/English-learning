package com.example.controller;

import java.util.List;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Test;
import com.example.entity.Test.TestType;
import com.example.dto.TestDTO;
import com.example.entity.Topic;
import com.example.service.TestService;

@RestController
@RequestMapping("/api/tests")
public class TestController {

    private final TestService testService;

    @Autowired
    public TestController(TestService testService) {
        this.testService = testService;
    }

    // Tạo bài test mới
    @PostMapping
    public ResponseEntity<?> createTest(@RequestBody TestDTO testDTO) {
        try {
            Test test = new Test();
            if (testDTO.getTestType() != null) {
                test.setTestType(testDTO.getTestType());
            }
            if (testDTO.getNumQuestions() != null) {
                test.setNumQuestions(testDTO.getNumQuestions());
            }
            if (testDTO.getTopicId() != null) {
                Topic topic = new Topic();
                topic.setTopicId(java.util.UUID.fromString(testDTO.getTopicId()));
                test.setTopic(topic);
            }
            Test createdTest = testService.createTest(test);
            return new ResponseEntity<>(createdTest, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (TestService.ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Lấy bài test theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Test> getTestById(@PathVariable UUID id) {
        Test test = testService.getTestById(id);
        return ResponseEntity.ok(test);
    }

    // Cập nhật bài test
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTest(@PathVariable UUID id, @RequestBody TestDTO testDTO) {
        try {
            Test details = new Test();
            if (testDTO.getTestType() != null) {
                details.setTestType(testDTO.getTestType());
            }
            if (testDTO.getNumQuestions() != null) {
                details.setNumQuestions(testDTO.getNumQuestions());
            }
            if (testDTO.getTopicId() != null) {
                Topic topic = new Topic();
                topic.setTopicId(java.util.UUID.fromString(testDTO.getTopicId()));
                details.setTopic(topic);
            }
            Test updatedTest = testService.updateTest(id, details);
            return ResponseEntity.ok(updatedTest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (TestService.ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Xóa bài test
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTest(@PathVariable UUID id) {
        testService.deleteTest(id);
        return ResponseEntity.noContent().build();
    }

    // Lấy tất cả bài test
    @GetMapping
    public ResponseEntity<List<Test>> getAllTests() {
        List<Test> tests = testService.getAllTests();
        return ResponseEntity.ok(tests);
    }

    // Lấy bài test theo topic
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<Test>> getTestsByTopic(@PathVariable UUID topicId) {
        List<Test> tests = testService.getTestsByTopic(topicId);
        return ResponseEntity.ok(tests);
    }

    // Lấy bài test theo loại
    @GetMapping("/type/{testType}")
    public ResponseEntity<List<Test>> getTestsByType(@PathVariable TestType testType) {
        List<Test> tests = testService.getTestsByType(testType);
        return ResponseEntity.ok(tests);
    }

    // Lấy tất cả bài test với phân trang
    @GetMapping("/paginated")
    public ResponseEntity<Page<Test>> getAllTestsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Test> tests = testService.getAllTestsPaginated(pageable);
        return ResponseEntity.ok(tests);
    }

    // Lấy bài test theo topic với phân trang
    @GetMapping("/topic/{topicId}/paginated")
    public ResponseEntity<Page<Test>> getTestsByTopicPaginated(
            @PathVariable UUID topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Test> tests = testService.getTestsByTopicPaginated(topicId, pageable);
        return ResponseEntity.ok(tests);
    }

    // Lấy bài test theo loại với phân trang
    @GetMapping("/type/{testType}/paginated")
    public ResponseEntity<Page<Test>> getTestsByTypePaginated(
            @PathVariable TestType testType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Test> tests = testService.getTestsByTypePaginated(testType, pageable);
        return ResponseEntity.ok(tests);
    }

    // Lấy bài test theo topic và loại với phân trang
    @GetMapping("/topic/{topicId}/type/{testType}/paginated")
    public ResponseEntity<Page<Test>> getTestsByTopicAndTypePaginated(
            @PathVariable UUID topicId,
            @PathVariable TestType testType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Test> tests = testService.getTestsByTopicAndTypePaginated(topicId, testType, pageable);
        return ResponseEntity.ok(tests);
    }

    // Lấy số lượng bài test theo topic
    @GetMapping("/topic/{topicId}/count")
    public ResponseEntity<Long> countTestsByTopic(@PathVariable UUID topicId) {
        Long count = testService.countTestsByTopic(topicId);
        return ResponseEntity.ok(count);
    }

    // Lấy các bài test mới nhất
    @GetMapping("/recent")
    public ResponseEntity<List<Test>> getRecentTests(
            @RequestParam(defaultValue = "5") int limit) {
        List<Test> tests = testService.getRecentTests(limit);
        return ResponseEntity.ok(tests);
    }
}
