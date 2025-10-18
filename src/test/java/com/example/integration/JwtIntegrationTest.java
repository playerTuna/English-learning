package com.example.integration;

import java.time.OffsetDateTime;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.entity.User;
import com.example.entity.User.UserRole;
import com.example.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.ANY)
@ActiveProfiles("jwt")
class JwtIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        User user = new User();
        user.setUsername("jwtuser");
        user.setPasswordHash(passwordEncoder.encode("password123"));
        user.setRole(UserRole.user);
        user.setName("JWT User");
        userRepository.save(user);
    }

    @Test
    void loginReturnsToken() throws Exception {
        String requestBody = "{\"username\":\"jwtuser\",\"password\":\"password123\"}";

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        String token = node.path("data").path("token").asText();
        assert token != null && !token.isEmpty();
    }

    @Test
    void accessProtectedWithoutTokenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/topics"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessProtectedWithValidTokenSucceeds() throws Exception {
        String requestBody = "{\"username\":\"jwtuser\",\"password\":\"password123\"}";
        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(response).path("data").path("token").asText();

        mockMvc.perform(get("/api/topics")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void accessProtectedWithInvalidTokenUnauthorized() throws Exception {
        String invalidToken = "invalid.header.payload";
        mockMvc.perform(get("/api/topics")
                .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessProtectedWithValidTokenAndInvalidRole() throws Exception {
        String requestBody = "{\"username\":\"jwtuser\",\"password\":\"password123\"}";
        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(response).path("data").path("token").asText();

        mockMvc.perform(get("/api/admin/topics")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void accessProtectedWithExpiredTokenUnauthorized() throws Exception {
        // tạo token đã hết hạn bằng cách tự ký (JWTUtils hoặc trực tiếp JJWT)
        String expiredToken = Jwts.builder()
                .subject("jwtuser")
                .issuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // 1h trước
                .expiration(new Date(System.currentTimeMillis() - 1000)) // đã hết hạn
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/topics")
                .header("Authorization", "Bearer " + expiredToken))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void adminCanBanUserUsingRequestParams() throws Exception {

        User admin = new User();
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setRole(UserRole.admin);
        admin.setName("Admin User");
        userRepository.save(admin);

        User normalUser = new User();
        normalUser.setUsername("john");
        normalUser.setPasswordHash(passwordEncoder.encode("john123"));
        normalUser.setRole(UserRole.user);
        normalUser.setName("John Doe");
        userRepository.save(normalUser);

        String loginBody = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        String loginResponse = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).path("data").path("token").asText();

        String reason = "Spamming content";
        String banUntil = OffsetDateTime.now().plusDays(7).toString();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/users/" + normalUser.getUserId() + "/ban")
                .param("banUntil", banUntil)
                .param("reason", reason)
                .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk());

        User bannedUser = userRepository.findById(normalUser.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        assertTrue(bannedUser.getBanStatus());
        assertEquals(reason, bannedUser.getReason());
        assertNotNull(bannedUser.getBanUtil());
    }

    @Test
    void adminCanUnbanUser() throws Exception {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setRole(UserRole.admin);
        admin.setName("Tuna");
        userRepository.save(admin);

        User banned = new User();
        banned.setUsername("bannedUser");
        banned.setPasswordHash(passwordEncoder.encode("test123"));
        banned.setRole(UserRole.user);
        banned.setBanStatus(true);
        banned.setReason("Violation");
        banned.setBanUtil(OffsetDateTime.now().plusDays(3));
        userRepository.save(banned);

        String loginBody = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        String loginResponse = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(loginResponse).path("data").path("token").asText();

        // gọi API unban
        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/users/" + banned.getUserId() + "/unban")
                .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk());

        User unbanned = userRepository.findById(banned.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        assertFalse(unbanned.getBanStatus());
        assertNull(unbanned.getReason());
        assertNull(unbanned.getBanUtil());
    }

    @Test
    void swapAdminRole() throws Exception {
        User admin = new User();
        admin.setUsername("Tuna");
        admin.setPasswordHash(passwordEncoder.encode("I@mnem0610"));
        admin.setRole(UserRole.admin);
        admin.setName("Võ Tiến Nam");
        userRepository.save(admin);

        User admin2 = new User();
        admin2.setUsername("Eri");
        admin2.setPasswordHash(passwordEncoder.encode("I@mnem0610"));
        admin2.setRole(UserRole.user);
        admin2.setName("Nam Vo Tien");
        userRepository.save(admin2);

        String loginBody1 = "{\"username\":\"Tuna\",\"password\":\"I@mnem0610\"}";
        String loginResponse1 = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        String token1 = objectMapper.readTree(loginResponse1).path("data").path("token").asText();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/users/" + admin2.getUserId() + "/role")
                .param("role", "admin")
                .header("Authorization", "Bearer " + token1))
                .andExpect(MockMvcResultMatchers.status().isOk());

        String loginBody2 = "{\"username\":\"Eri\",\"password\":\"I@mnem0610\"}";
        String loginResponse2 = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        String token2 = objectMapper.readTree(loginResponse2).path("data").path("token").asText();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/users/" + admin.getUserId() + "/role")
                .param("role", "user")
                .header("Authorization", "Bearer " + token2))
                .andExpect(MockMvcResultMatchers.status().isOk());

        User checkRole1 = userRepository.findById(admin.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User checkRole2 = userRepository.findById(admin2.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("Tuna role: " + checkRole1.getRole());
        System.out.println("Eri role: " + checkRole2.getRole());

        Assertions.assertEquals(UserRole.user, checkRole1.getRole());
        Assertions.assertEquals(UserRole.admin, checkRole2.getRole());
    }
}
