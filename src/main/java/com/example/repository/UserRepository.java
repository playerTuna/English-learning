package com.example.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.User;
import com.example.entity.User.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUserId(UUID userId);

    boolean existsByUsername(String username);

    List<User> findByRole(UserRole role);

    Page<User> findByRole(UserRole role, Pageable pageable);

    List<User> findByNameContaining(String keyword);

    Page<User> findByNameContaining(String keyword, Pageable pageable);

    void deleteByUserId(UUID id);

    @Query("SELECT u FROM User u WHERE (:role IS NULL OR u.role = :role) AND (:name IS NULL OR u.name LIKE %:name%)")
    Page<User> searchUsers(
            @Param("role") UserRole role,
            @Param("name") String name,
            Pageable pageable);
            
    long countByRole(UserRole role);
}