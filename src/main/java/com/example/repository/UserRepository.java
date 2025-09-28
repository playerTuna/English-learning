package com.example.repository;

import com.example.entity.User;
import com.example.entity.User.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Tìm user theo username
    Optional<User> findByUsername(String username);

    // Tìm user theo ID
    Optional<User> findByUserId(UUID userId);

    // Kiểm tra username đã tồn tại chưa
    boolean existsByUsername(String username);

    // Tìm theo role
    List<User> findByRole(UserRole role);

    // Tìm theo role với phân trang
    Page<User> findByRole(UserRole role, Pageable pageable);

    // Tìm theo tên
    List<User> findByNameContaining(String keyword);

    // Tìm theo tên với phân trang
    Page<User> findByNameContaining(String keyword, Pageable pageable);

    // Xóa user theo ID
    void deleteByUserId(UUID id);

    // Tìm kiếm user theo role và tên
    @Query("SELECT u FROM User u WHERE (:role IS NULL OR u.role = :role) AND (:name IS NULL OR u.name LIKE %:name%)")
    Page<User> searchUsers(
            @Param("role") UserRole role,
            @Param("name") String name,
            Pageable pageable);

    // Đếm số lượng user theo role
    long countByRole(UserRole role);
}