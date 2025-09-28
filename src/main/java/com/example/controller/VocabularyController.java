package com.example.controller;

import java.util.UUID;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import com.example.entity.Vocabulary;
import com.example.entity.Vocabulary.WordType;
import com.example.service.VocabularyService;

@RestController
@RequestMapping("/api/vocabulary")
public class VocabularyController {

    private final VocabularyService vocabularyService;

    @Autowired
    public VocabularyController(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }

    @PostMapping
    public ResponseEntity<Vocabulary> createVocabulary(@RequestBody Vocabulary vocabulary) {
        Vocabulary newVocab = vocabularyService.createVocabulary(vocabulary);
        return new ResponseEntity<>(newVocab, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vocabulary> getVocabularyById(@PathVariable UUID id) {
        Vocabulary vocab = vocabularyService.getVocabularyById(id);
        return ResponseEntity.ok(vocab);
    }

    @GetMapping("/word/{word}")
    public ResponseEntity<Vocabulary> getVocabularyByWord(@PathVariable String word) {
        Vocabulary vocab = vocabularyService.getVocabularyByWord(word);
        return ResponseEntity.ok(vocab);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vocabulary> updateVocabulary(
            @PathVariable UUID id,
            @RequestBody Vocabulary vocabulary) {
        Vocabulary updatedVocab = vocabularyService.updateVocabulary(id, vocabulary);
        return ResponseEntity.ok(updatedVocab);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVocabulary(@PathVariable UUID id) {
        vocabularyService.deleteVocabulary(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Vocabulary>> getAllVocabulary() {
        List<Vocabulary> vocabularies = vocabularyService.getAllVocabulary();
        return ResponseEntity.ok(vocabularies);
    }

    @GetMapping("/topic/{topicName}")
    public ResponseEntity<List<Vocabulary>> getVocabularyByTopic(@PathVariable String topicName) {
        List<Vocabulary> vocabularies = vocabularyService.getVocabularyByTopic(topicName);
        return ResponseEntity.ok(vocabularies);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Vocabulary>> searchVocabulary(
            @RequestParam(required = false) String word,
            @RequestParam(required = false) String meaning,
            @RequestParam(required = false) WordType wordType) {
        List<Vocabulary> vocabularies = vocabularyService.searchVocabulary(word, meaning, wordType);
        return ResponseEntity.ok(vocabularies);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Vocabulary>> getAllVocabularyPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "word") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Vocabulary> vocabularies = vocabularyService.getAllVocabularyPaginated(pageable);
        return ResponseEntity.ok(vocabularies);
    }

    @GetMapping("/topic/{topicName}/paginated")
    public ResponseEntity<Page<Vocabulary>> getVocabularyByTopicPaginated(
            @PathVariable String topicName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "word") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Vocabulary> vocabularies = vocabularyService.getVocabularyByTopicPaginated(topicName, pageable);
        return ResponseEntity.ok(vocabularies);
    }
}
