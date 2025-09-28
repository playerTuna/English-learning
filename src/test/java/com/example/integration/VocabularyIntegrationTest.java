package com.example.integration;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.entity.Topic;
import com.example.entity.Vocabulary;
import com.example.entity.Vocabulary.WordType;
import com.example.repository.TopicRepository;
import com.example.repository.VocabularyRepository;

public class VocabularyIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private TopicRepository topicRepository;

    private Topic testTopic;
    private Vocabulary testVocabulary;

    @BeforeEach
    void setUp() {
        // Create test topic
        testTopic = new Topic();
        testTopic.setName("Test Topic");
        testTopic = topicRepository.save(testTopic);

        // Create test vocabulary
        testVocabulary = new Vocabulary();
        testVocabulary.setWord("integration");
        testVocabulary.setMeaning("A test word for integration");
        testVocabulary.setWordType(WordType.noun);
        testVocabulary.setTopic(testTopic);
    }

    @Test
    void testCreateAndRetrieveVocabulary() throws Exception {
        // Create vocabulary through API with authentication
        String response = mockMvc.perform(authenticated(post("/api/vocabulary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testVocabulary))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.word").value(testVocabulary.getWord()))
                .andExpect(jsonPath("$.meaning").value(testVocabulary.getMeaning()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract ID from response
        Vocabulary createdVocab = objectMapper.readValue(response, Vocabulary.class);
        UUID vocabId = createdVocab.getVocabId();

        // Verify vocabulary exists in database
        assertTrue(vocabularyRepository.findById(vocabId).isPresent());

        // Retrieve vocabulary through API
        mockMvc.perform(authenticated(get("/api/vocabulary/{id}", vocabId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.word").value(testVocabulary.getWord()))
                .andExpect(jsonPath("$.meaning").value(testVocabulary.getMeaning()))
                .andExpect(jsonPath("$.wordType").value(testVocabulary.getWordType().toString()))
                .andExpect(jsonPath("$.topic.name").value(testTopic.getName()));
    }

    @Test
    void testCreateAndRetrieveByTopic() throws Exception {
        // Save vocabulary to database
        testVocabulary = vocabularyRepository.save(testVocabulary);

        // Retrieve vocabularies by topic through API
        mockMvc.perform(authenticated(get("/api/vocabulary/topic/{topicName}", testTopic.getName())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].word").value(testVocabulary.getWord()))
                .andExpect(jsonPath("$[0].topic.name").value(testTopic.getName()));
    }

    @Test
    void testUpdateVocabulary() throws Exception {
        // Save initial vocabulary
        testVocabulary = vocabularyRepository.save(testVocabulary);
        UUID vocabId = testVocabulary.getVocabId();

        // Update vocabulary data
        testVocabulary.setWord("updated");
        testVocabulary.setMeaning("Updated meaning");
        testVocabulary.setWordType(WordType.verb);

        // Send update request through API
        mockMvc.perform(authenticated(put("/api/vocabulary/{id}", vocabId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testVocabulary))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.word").value("updated"))
                .andExpect(jsonPath("$.meaning").value("Updated meaning"))
                .andExpect(jsonPath("$.wordType").value("verb"));

        // Verify update in database
        Vocabulary updatedVocab = vocabularyRepository.findById(vocabId).orElseThrow();
        assertEquals("updated", updatedVocab.getWord());
        assertEquals("Updated meaning", updatedVocab.getMeaning());
        assertEquals(WordType.verb, updatedVocab.getWordType());
    }

    @Test
    void testDeleteVocabulary() throws Exception {
        // Save vocabulary to database
        testVocabulary = vocabularyRepository.save(testVocabulary);
        UUID vocabId = testVocabulary.getVocabId();

        // Delete through API
        mockMvc.perform(authenticated(delete("/api/vocabulary/{id}", vocabId)))
                .andExpect(status().isNoContent());

        // Verify deletion in database
        assertFalse(vocabularyRepository.findById(vocabId).isPresent());
    }

    @Test
    void testSearchVocabulary() throws Exception {
        // Save test vocabularies
        testVocabulary = vocabularyRepository.save(testVocabulary);

        Vocabulary testVocab2 = new Vocabulary();
        testVocab2.setWord("testing");
        testVocab2.setMeaning("Another test word");
        testVocab2.setWordType(WordType.verb);
        testVocab2.setTopic(testTopic);
        vocabularyRepository.save(testVocab2);

        // Test search by word
        mockMvc.perform(authenticated(get("/api/vocabulary/search")
                .param("word", "test")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].word").value("integration"))
                .andExpect(jsonPath("$[1].word").value("testing"));

        // Test search by meaning
        mockMvc.perform(authenticated(get("/api/vocabulary/search")
                .param("meaning", "test word")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].meaning").value(containsString("test word")));

        // Test search by word type
        mockMvc.perform(authenticated(get("/api/vocabulary/search")
                .param("wordType", "verb")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].word").value("testing"))
                .andExpect(jsonPath("$[0].wordType").value("verb"));
    }

    @Test
    void testPaginatedSearch() throws Exception {
        // Save multiple vocabularies
        for (int i = 0; i < 15; i++) {
            Vocabulary vocab = new Vocabulary();
            vocab.setWord("word" + i);
            vocab.setMeaning("meaning " + i);
            vocab.setWordType(WordType.noun);
            vocab.setTopic(testTopic);
            vocabularyRepository.save(vocab);
        }

        // Test first page
        mockMvc.perform(authenticated(get("/api/vocabulary/paginated")
                .param("page", "0")
                .param("size", "10")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(2));

        // Test second page
        mockMvc.perform(get("/api/vocabulary/paginated")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.totalElements").value(15));

        // Test sorting
        mockMvc.perform(authenticated(get("/api/vocabulary/paginated")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "word")
                .param("sortDir", "desc")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].word").value("word9"));
    }
}