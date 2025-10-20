package com.example.integration;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.example.config.TestApplication;
import com.example.dto.TestResultDTO;
import com.example.entity.TestResult;
import com.example.entity.Topic;
import com.example.entity.User;
import com.example.entity.User.UserRole;
import com.example.repository.TestRepository;
import com.example.repository.TestResultRepository;
import com.example.repository.TopicRepository;
import com.example.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = com.example.config.TestApplication.class)
@AutoConfigureMockMvc
@Transactional
public class TestResultIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestResultRepository testResultRepository;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private TestResult testResult;
    private com.example.entity.Test testEntity;
    private User user;
    private Topic topic;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Create test topic
        topic = new Topic();
        topic.setName("Test Topic");
        topic.setDescription("Test Description");
        topic = topicRepository.save(topic);

        testEntity = new com.example.entity.Test();
        testEntity.setTestType(com.example.entity.Test.TestType.multiple_choice);
        testEntity.setNumQuestions(10);
        testEntity.setTopic(topic);
        testEntity = testRepository.save(testEntity);

        user = new User();
        user.setUsername("testuser");
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setRole(UserRole.user);
        user.setCreatedAt(OffsetDateTime.now());
        user = userRepository.save(user);

        testResult = new TestResult();
        testResult.setTest(testEntity);
        testResult.setUser(user);
        testResult.setCorrectAnswers(8);
        testResult.setTotalQuestions(10);
        testResult.setScore(80);
        testResult.setTakenAt(OffsetDateTime.now());
        testResult.setCreatedAt(OffsetDateTime.now());
        testResult = testResultRepository.save(testResult);
    }

    @Test
    void testCreateAndRetrieveTestResult() throws Exception {
        TestResultDTO newTestResult = new TestResultDTO();
        newTestResult.setTestId(testEntity.getTestId().toString());
        newTestResult.setUserId(user.getUserId().toString());
        newTestResult.setCorrectAnswers(36);
        newTestResult.setTotalQuestions(40);
        newTestResult.setTakenAt(OffsetDateTime.now());

        String response = mockMvc.perform(post("/api/test-results")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTestResult)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.score").value(90))
                .andReturn()
                .getResponse()
                .getContentAsString();

        TestResult createdTestResult = objectMapper.readValue(response, TestResult.class);

        mockMvc.perform(get("/api/test-results/{id}", createdTestResult.getResultId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(90));
    }

    @Test
    void testGetTestResultsByUser() throws Exception {
        mockMvc.perform(get("/api/test-results/user/{userId}", user.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].score").value(80.0));
    }

    @Test
    void testGetTestResultsByTest() throws Exception {
        mockMvc.perform(get("/api/test-results/test/{testId}", testEntity.getTestId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].score").value(80.0));
    }

    @Test
    void testUpdateTestResult() throws Exception {
        TestResultDTO updateTestResult = new TestResultDTO();
        updateTestResult.setTestId(testEntity.getTestId().toString());
        updateTestResult.setUserId(user.getUserId().toString());
        updateTestResult.setCorrectAnswers(9);
        updateTestResult.setTotalQuestions(10);

        mockMvc.perform(put("/api/test-results/{id}", testResult.getResultId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTestResult)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(90));
    }

    @Test
    void testDeleteTestResult() throws Exception {
        mockMvc.perform(delete("/api/test-results/{id}", testResult.getResultId()))
                .andExpect(status().isNoContent());

        assertFalse(testResultRepository.existsById(testResult.getResultId()));
    }

    @Test
    void testCreateTestResultWithInvalidUser() throws Exception {
        TestResultDTO newTestResult = new TestResultDTO();
        newTestResult.setTestId(testEntity.getTestId().toString());
        newTestResult.setUserId(UUID.randomUUID().toString());
        newTestResult.setCorrectAnswers(9);
        newTestResult.setTotalQuestions(10);

        mockMvc.perform(post("/api/test-results")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTestResult)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTestResultWithInvalidTest() throws Exception {
        TestResultDTO newTestResult = new TestResultDTO();
        newTestResult.setTestId(UUID.randomUUID().toString());
        newTestResult.setUserId(user.getUserId().toString());
        newTestResult.setCorrectAnswers(9);
        newTestResult.setTotalQuestions(10);

        mockMvc.perform(post("/api/test-results")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTestResult)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTestResultWithInvalidCorrectAnswers() throws Exception {
        TestResultDTO newTestResult = new TestResultDTO();
        newTestResult.setTestId(testEntity.getTestId().toString());
        newTestResult.setUserId(user.getUserId().toString());
        newTestResult.setCorrectAnswers(-1); // Invalid correct answers (negative)
        newTestResult.setTotalQuestions(10);

        mockMvc.perform(post("/api/test-results")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTestResult)))
                .andExpect(status().isBadRequest());
    }
}