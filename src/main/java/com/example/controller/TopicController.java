
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Topic;
import com.example.exception.ResourceNotFoundException;
import com.example.service.TopicService;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    private final TopicService topicService;

    @Autowired
    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic) {
        Topic newTopic = topicService.createTopic(topic);
        return new ResponseEntity<>(newTopic, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Topic> getTopicById(@PathVariable UUID id) {
        Topic topic = topicService.getTopicById(id);
        return ResponseEntity.ok(topic);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Topic> getTopicByName(@PathVariable String name) {
        Topic topic = topicService.getTopicByName(name);
        return ResponseEntity.ok(topic);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Topic> updateTopic(@PathVariable UUID id, @RequestBody Topic topic) {
        Topic updatedTopic = topicService.updateTopic(id, topic);
        return ResponseEntity.ok(updatedTopic);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable UUID id) {
        topicService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Topic>> getAllTopics() {
        List<Topic> topics = topicService.getAllTopics();
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Topic>> searchTopics(@RequestParam String keyword) {
        List<Topic> topics = topicService.searchTopics(keyword);
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Topic>> getAllTopicsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Topic> topics = topicService.getAllTopicsPaginated(pageable);
        return ResponseEntity.ok(topics);
    }
}
