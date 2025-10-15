package com.example.controller;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.UserDTO;
import com.example.entity.User;
import com.example.service.UserService;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('admin')")
public class AdminController {

    @Autowired
    UserService userService;

    @GetMapping
    public List<UserDTO> listUsers() {
        return userService.getAllUsers();
    }

    // Change role
    @PutMapping("/{id}/role")
    public ResponseEntity<?> changeRole(@PathVariable UUID id,
            @RequestParam User.UserRole role) {
        userService.changeRole(id, role);
        return ResponseEntity.ok().build();
    }

    // Ban (khóa) 1 user
    @PutMapping("/{id}/ban")
    public ResponseEntity<?> banUser(
            @PathVariable UUID id,
            @RequestBody BanRequest request
    ) {
        userService.banUser(id, request.getBanUntil(), request.getReason());
        return ResponseEntity.ok().build();
    }

}
