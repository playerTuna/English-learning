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

import com.example.entity.Topic;
import com.example.repository.TopicRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = com.example.config.TestApplication.class)
@AutoConfigureMockMvc  
@Transactional
public class TopicIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Topic testTopic;

    @BeforeEach
    void setUp() {
        testTopic = new Topic();
        testTopic.setName("Test Topic");
        testTopic.setDescription("Test Description");
        testTopic = topicRepository.save(testTopic);
    }

    @Test
    void testCreateAndRetrieveTopic() throws Exception {
        Topic newTopic = new Topic();
        newTopic.setName("New Topic");
        newTopic.setDescription("New Description");

        // Create topic
        String response = mockMvc.perform(post("/api/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTopic)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Topic"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Topic createdTopic = objectMapper.readValue(response, Topic.class);

        // Retrieve topic
        mockMvc.perform(get("/api/topics/{id}", createdTopic.getTopicId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Topic"))
                .andExpect(jsonPath("$.description").value("New Description"));
    }

    @Test
    void testUpdateTopic() throws Exception {
        testTopic.setName("Updated Topic");
        testTopic.setDescription("Updated Description");

        mockMvc.perform(put("/api/topics/{id}", testTopic.getTopicId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTopic)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Topic"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    void testDeleteTopic() throws Exception {
        mockMvc.perform(delete("/api/topics/{id}", testTopic.getTopicId()))
                .andExpect(status().isNoContent());

        assertFalse(topicRepository.existsById(testTopic.getTopicId()));
    }

    @Test
    void testGetAllTopics() throws Exception {
        // Create additional topic
        Topic anotherTopic = new Topic();
        anotherTopic.setName("Another Topic");
        anotherTopic.setDescription("Another Description");
        topicRepository.save(anotherTopic);

        mockMvc.perform(get("/api/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testTopicNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        mockMvc.perform(get("/api/topics/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}