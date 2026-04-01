package com.utez.misestadias.controller;

import com.utez.misestadias.dto.StudentProfileDTO;
import com.utez.misestadias.service.StudentProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<StudentProfileDTO> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No se encontró sesión activa");
        }

        return ResponseEntity.ok(profileService.getMyProfile(userDetails.getUsername()));
    }

    @PutMapping
    public ResponseEntity<StudentProfileDTO> upsertProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody StudentProfileDTO dto) {

        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No se encontró sesión activa");
        }

        return ResponseEntity.ok(profileService.upsertProfile(userDetails.getUsername(), dto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<StudentProfileDTO> getProfileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getProfileByUserId(userId));
    }
}