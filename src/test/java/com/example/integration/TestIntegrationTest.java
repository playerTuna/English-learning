package com.example.integration;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.TestDTO;
import com.example.entity.Topic;
import com.example.repository.TestRepository;
import com.example.repository.TopicRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = com.example.config.TestApplication.class)
@AutoConfigureMockMvc
@Transactional
public class TestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private com.example.entity.Test testEntity;
    private Topic testTopic;

    @BeforeEach
    void setUp() {
        // Create test topic
        testTopic = new Topic();
        testTopic.setName("Test Topic");
        testTopic.setDescription("Test Description");
        testTopic = topicRepository.save(testTopic);

        // Create test entity
        testEntity = new com.example.entity.Test();
        testEntity.setTestType(com.example.entity.Test.TestType.multiple_choice);
        testEntity.setNumQuestions(10);
        testEntity.setTopic(testTopic);
        testEntity.setCreatedAt(java.time.OffsetDateTime.now());
        testEntity = testRepository.save(testEntity);
    }

    @Test
    void testCreateAndRetrieveTest() throws Exception {
        TestDTO newTest = new TestDTO();
        newTest.setTestType(com.example.entity.Test.TestType.multiple_choice);
        newTest.setNumQuestions(5);
        newTest.setTopicId(testTopic.getTopicId().toString());

        // Create test
        String response = mockMvc.perform(post("/api/tests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numQuestions").value(5))
                .andReturn()
                .getResponse()
                .getContentAsString();

        com.example.entity.Test createdTest = objectMapper.readValue(response, com.example.entity.Test.class);

        // Retrieve test
        mockMvc.perform(get("/api/tests/{id}", createdTest.getTestId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.testType").value("multiple_choice"))
                .andExpect(jsonPath("$.numQuestions").value(5));
    }

    @Test
    void testUpdateTest() throws Exception {
        TestDTO updateTest = new TestDTO();
        updateTest.setTestType(com.example.entity.Test.TestType.fill_in_the_blank);
        updateTest.setNumQuestions(15);
        updateTest.setTopicId(testTopic.getTopicId().toString());

        mockMvc.perform(put("/api/tests/{id}", testEntity.getTestId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.testType").value("fill_in_the_blank"))
                .andExpect(jsonPath("$.numQuestions").value(15));
    }

    @Test
    void testDeleteTest() throws Exception {
        mockMvc.perform(delete("/api/tests/{id}", testEntity.getTestId()))
                .andExpect(status().isNoContent());

        assertFalse(testRepository.existsById(testEntity.getTestId()));
    }

    @Test
    void testGetTestsByTopic() throws Exception {
        mockMvc.perform(get("/api/tests/topic/{topicId}", testTopic.getTopicId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].testId").value(testEntity.getTestId().toString()));
    }

    @Test
    void testGetAllTests() throws Exception {
        mockMvc.perform(get("/api/tests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testCreateTestWithInvalidTopic() throws Exception {
        TestDTO newTest = new TestDTO();
        newTest.setTestType(com.example.entity.Test.TestType.multiple_choice);
        newTest.setNumQuestions(5);
        newTest.setTopicId(UUID.randomUUID().toString()); // Non-existent topic ID

        mockMvc.perform(post("/api/tests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTestWithInvalidQuestionCount() throws Exception {
        TestDTO newTest = new TestDTO();
        newTest.setTestType(com.example.entity.Test.TestType.multiple_choice);
        newTest.setNumQuestions(0); // Invalid question count
        newTest.setTopicId(testTopic.getTopicId().toString());

        mockMvc.perform(post("/api/tests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTest)))
                .andExpect(status().isBadRequest());
    }
}