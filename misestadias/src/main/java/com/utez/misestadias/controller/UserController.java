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

/**
 * src/main/java/com/utez/misestadias/controller/UserController.java
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /api/users/students
    @GetMapping("/students")
    public ResponseEntity<List<StudentProfileDTO>> getAllStudents() {
        return ResponseEntity.ok(userService.getAllStudents());
    }

    // GET /api/users
    @GetMapping
    public ResponseEntity<List<StudentProfileDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // POST /api/users — crear nuevo usuario
    @PostMapping
    public ResponseEntity<StudentProfileDTO> createUser(@Valid @RequestBody CreateUserDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(dto));
    }

    // DELETE /api/users/{id} — desactivar usuario (soft delete)
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> deactivateUser(@PathVariable Long userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.ok(Map.of("message", "Usuario desactivado correctamente."));
    }
}
