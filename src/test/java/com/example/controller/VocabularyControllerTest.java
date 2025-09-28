package com.example.controller;

import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.entity.Vocabulary;
import com.example.entity.Vocabulary.WordType;
import com.example.service.VocabularyService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(VocabularyController.class)
public class VocabularyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private VocabularyService vocabularyService;

    @Autowired
    private ObjectMapper objectMapper;

    private Vocabulary testVocabulary;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testVocabulary = new Vocabulary();
        testVocabulary.setVocabId(testId);
        testVocabulary.setWord("test");
        testVocabulary.setMeaning("A test word");
        testVocabulary.setWordType(WordType.noun);
    }

    @Test
    void testCreateVocabulary() throws Exception {
        when(vocabularyService.createVocabulary(any(Vocabulary.class))).thenReturn(testVocabulary);

        mockMvc.perform(post("/api/vocabulary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testVocabulary)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.word").value(testVocabulary.getWord()))
                .andExpect(jsonPath("$.meaning").value(testVocabulary.getMeaning()));
    }

    @Test
    void testGetVocabularyById() throws Exception {
        when(vocabularyService.getVocabularyById(testId)).thenReturn(testVocabulary);

        mockMvc.perform(get("/api/vocabulary/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.word").value(testVocabulary.getWord()));
    }

    @Test
    void testUpdateVocabulary() throws Exception {
        when(vocabularyService.updateVocabulary(any(UUID.class), any(Vocabulary.class)))
                .thenReturn(testVocabulary);

        mockMvc.perform(put("/api/vocabulary/{id}", testId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testVocabulary)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.word").value(testVocabulary.getWord()));
    }

    @Test
    void testDeleteVocabulary() throws Exception {
        doNothing().when(vocabularyService).deleteVocabulary(testId);

        mockMvc.perform(delete("/api/vocabulary/{id}", testId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testSearchVocabulary() throws Exception {
        when(vocabularyService.searchVocabulary("test", "meaning", WordType.noun))
                .thenReturn(Arrays.asList(testVocabulary));

        mockMvc.perform(get("/api/vocabulary/search")
                .param("word", "test")
                .param("meaning", "meaning")
                .param("wordType", "noun"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].word").value(testVocabulary.getWord()));
    }

    @Test
    void testGetVocabularyByTopic() throws Exception {
        when(vocabularyService.getVocabularyByTopic("TestTopic"))
                .thenReturn(Arrays.asList(testVocabulary));

        mockMvc.perform(get("/api/vocabulary/topic/{topicName}", "TestTopic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].word").value(testVocabulary.getWord()));
    }

    @Test
    void testGetAllVocabularyPaginated() throws Exception {
        Page<Vocabulary> page = new PageImpl<>(Arrays.asList(testVocabulary));
        when(vocabularyService.getAllVocabularyPaginated(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/vocabulary/paginated")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "word")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].word").value(testVocabulary.getWord()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testGetVocabularyByTopicPaginated() throws Exception {
        Page<Vocabulary> page = new PageImpl<>(Arrays.asList(testVocabulary));
        when(vocabularyService.getVocabularyByTopicPaginated(any(String.class), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/vocabulary/topic/{topicName}/paginated", "TestTopic")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "word")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].word").value(testVocabulary.getWord()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}