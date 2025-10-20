package com.example.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.LoginRequestDTO;
import com.example.dto.UserDTO;
import com.example.entity.User;
import com.example.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = com.example.config.TestApplication.class)
@AutoConfigureMockMvc
@Transactional
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPasswordHash("testpassword"); // In real application, this would be hashed
        testUser.setName("Test User");
        testUser.setRole(User.UserRole.user);
        testUser = userRepository.save(testUser);
    }

    @Test
    void testRegisterUser() throws Exception {
    UserDTO newUser = new UserDTO();
    newUser.setUsername("newuser");
    newUser.setPassword("newpassword");
    newUser.setName("New User");

    mockMvc.perform(post("/api/users/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(newUser)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("newuser"))
        .andExpect(jsonPath("$.name").value("New User"));
    }

    @Test
    void testLogin() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername(testUser.getUsername());
        loginRequest.setPassword("testpassword");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testGetUserProfile() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(testUser.getUsername()))
                .andExpect(jsonPath("$.name").value(testUser.getName()));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDTO updateUser = new UserDTO();
        updateUser.setName("Updated Name");

        mockMvc.perform(put("/api/users/{id}", testUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", testUser.getUserId()))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.existsById(testUser.getUserId()));
    }

    @Test
    void testRegisterDuplicateUsername() throws Exception {
    UserDTO duplicateUser = new UserDTO();
    duplicateUser.setUsername(testUser.getUsername());
    duplicateUser.setPassword("anypassword");
    duplicateUser.setName("Duplicate User");

    mockMvc.perform(post("/api/users/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(duplicateUser)))
        .andExpect(status().isConflict());
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername(testUser.getUsername());
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}