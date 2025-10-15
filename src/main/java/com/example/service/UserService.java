package com.example.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.UserDTO;
import com.example.entity.User;
import com.example.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        return convertToDTO(user);
    }

    public void changeRole(UUID id, User.UserRole newRole) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found!"));
        user.setRole(newRole);
        userRepository.save(user);
    }

    public void banUser(UUID id, OffsetDateTime banUtil, String reason) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found!"));
        user.setBanStatus(true);
        user.setBanUtil(banUtil);
        user.setReason(reason);
        userRepository.save(user);
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        // Kiểm tra username đã tồn tại chưa
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username exists: " + userDTO.getUsername());
        }

        // Tạo user mới
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setName(userDTO.getName());
        user.setRole(userDTO.getRole());
        // Trong thực tế, bạn cần mã hóa mật khẩu tại đây
        user.setPasswordHash("default_password_hash");

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Transactional
    public UserDTO updateUser(UUID id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Cập nhật thông tin
        user.setName(userDTO.getName());
        if (userDTO.getRole() != null) {
            user.setRole(userDTO.getRole());
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    public UserDTO login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        // In a real application, you would check the password hash here
        if (password == null || !password.equals(user.getPasswordHash())) {
            throw new RuntimeException("Invalid username or password");
        }
        return convertToDTO(user);
    }

    @Transactional
    public UserDTO register(UserDTO userDTO) {
        if (userDTO.getUsername() == null || userDTO.getPassword() == null) {
            throw new RuntimeException("Username and password are required");
        }
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setName(userDTO.getName());
        user.setRole(userDTO.getRole());
        user.setPasswordHash(userDTO.getPassword());
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    // Helper method to convert Entity to DTO
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getUserId().toString(),
                user.getUsername(),
                user.getName(),
                user.getRole());
    }
}