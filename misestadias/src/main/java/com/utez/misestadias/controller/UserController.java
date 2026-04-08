package com.utez.misestadias.controller;

import com.utez.misestadias.dto.CreateUserDTO;
import com.utez.misestadias.dto.StudentProfileDTO;
import com.utez.misestadias.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/students")
    public ResponseEntity<List<StudentProfileDTO>> getAllStudents() {
        return ResponseEntity.ok(userService.getAllStudents());
    }

    @GetMapping
    public ResponseEntity<List<StudentProfileDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<StudentProfileDTO> createUser(@Valid @RequestBody CreateUserDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(dto));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> deactivateUser(@PathVariable Long userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.ok(Map.of("message", "Usuario desactivado correctamente."));
    }

    @PutMapping("/{userId}/activate")
    public ResponseEntity<Map<String, String>> activateUser(@PathVariable Long userId) {
        userService.activateUser(userId);
        return ResponseEntity.ok(Map.of("message", "Usuario reactivado correctamente."));
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<StudentProfileDTO> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody StudentProfileDTO dto) {
        return ResponseEntity.ok(userService.updateProfileByUserId(userId, dto));
    }
}