package com.example.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.entity.User;
import com.example.entity.User.UserRole;
import com.example.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

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
    void accessAdminWithAdminRoleSucceeds() throws Exception {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setRole(UserRole.admin);
        admin.setName("Admin User");
        userRepository.save(admin);

        String requestBody = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(response).path("data").path("token").asText();
        String adminResponse = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/admin/topics")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();

        System.out.println(">>> Response: " + adminResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/topics")
                .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
