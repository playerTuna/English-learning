package com.example.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.Topic;

@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID> {
    @Override
    Optional<Topic> findById(UUID id);
    
    Optional<Topic> findByName(String name);
    
    List<Topic> findByNameContaining(String keyword);
    
    Page<Topic> findByNameContaining(String keyword, Pageable pageable);
    
    boolean existsByName(String name);
    
    @Override
    Page<Topic> findAll(Pageable pageable);
}
