package com.example.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.RefreshToken;
import com.example.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID>{
    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);
}
