package com.example.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Topic;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.TopicRepository;

@Service
public class TopicService {
    
    private final TopicRepository topicRepository;
    
    @Autowired
    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }
    
    /**
     * Tạo chủ đề mới
     */
    @Transactional
    public Topic createTopic(Topic topic) {
        // Kiểm tra tên chủ đề đã tồn tại chưa
        if (topicRepository.existsByName(topic.getName())) {
            throw new RuntimeException("Topic name already exists");
        }
        
        return topicRepository.save(topic);
    }
    
    /**
     * Lấy chủ đề theo ID
     */
    public Topic getTopicById(UUID id) {
    return topicRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));
    }
    
    /**
     * Lấy chủ đề theo tên
     */
    public Topic getTopicByName(String name) {
    return topicRepository.findByName(name)
        .orElseThrow(() -> new ResourceNotFoundException("Topic not found with name: " + name));
    }
    
    /**
     * Cập nhật chủ đề
     */
    @Transactional
    public Topic updateTopic(UUID id, Topic topicDetails) {
        Topic topic = getTopicById(id);
        
        // Kiểm tra tên mới có bị trùng không
        if (topicDetails.getName() != null && !topicDetails.getName().equals(topic.getName())) {
            if (topicRepository.existsByName(topicDetails.getName())) {
                throw new RuntimeException("Topic name already exists");
            }
            topic.setName(topicDetails.getName());
        }
        
        // Cập nhật mô tả
        if (topicDetails.getDescription() != null) {
            topic.setDescription(topicDetails.getDescription());
        }
        
        return topicRepository.save(topic);
    }
    
    /**
     * Xóa chủ đề
     */
    @Transactional
    public void deleteTopic(UUID id) {
        if (!topicRepository.existsById(id)) {
            throw new RuntimeException("Topic not found with id: " + id);
        }
        topicRepository.deleteById(id);
    }
    
    /**
     * Lấy tất cả chủ đề
     */
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }
    
    /**
     * Tìm chủ đề theo từ khóa
     */
    public List<Topic> searchTopics(String keyword) {
        return topicRepository.findByNameContaining(keyword);
    }
    
    /**
     * Lấy tất cả chủ đề với phân trang
     */
    public Page<Topic> getAllTopicsPaginated(Pageable pageable) {
        return topicRepository.findAll(pageable);
    }
    
    /**
     * Tìm chủ đề theo từ khóa với phân trang
     */
    public Page<Topic> searchTopicsPaginated(String keyword, Pageable pageable) {
        return topicRepository.findByNameContaining(keyword, pageable);
    }
}