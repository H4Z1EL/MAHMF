package com.utez.misestadias.controller;

import com.utez.misestadias.dto.ActivityRequestDTO;
import com.utez.misestadias.dto.ActivityResponseDTO;
import com.utez.misestadias.dto.AttachmentResponseDTO;
import com.utez.misestadias.dto.StatusUpdateDTO;
import com.utez.misestadias.service.ActivityService;
import com.utez.misestadias.service.AttachmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final AttachmentService attachmentService;

    @PostMapping
    public ResponseEntity<ActivityResponseDTO> createActivity(
            @Valid @RequestBody ActivityRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(activityService.createActivity(dto));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ActivityResponseDTO>> getActivitiesByStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(activityService.getActivitiesByStudent(studentId));
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponseDTO> getActivityById(
            @PathVariable Long activityId) {
        return ResponseEntity.ok(activityService.getActivityById(activityId));
    }

    @PutMapping("/{activityId}/status")
    public ResponseEntity<ActivityResponseDTO> updateStatus(
            @PathVariable Long activityId,
            @Valid @RequestBody StatusUpdateDTO dto) {
        return ResponseEntity.ok(activityService.updateStatus(activityId, dto));
    }

    @PostMapping(value = "/{activityId}/upload", consumes = "multipart/form-data")
    public ResponseEntity<AttachmentResponseDTO> uploadFile(
            @PathVariable Long activityId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {

        String studentEmail = userDetails.getUsername();
        AttachmentResponseDTO response = attachmentService.uploadFile(activityId, file, studentEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{activityId}/attachments")
    public ResponseEntity<List<AttachmentResponseDTO>> getAttachments(
            @PathVariable Long activityId) {
        return ResponseEntity.ok(attachmentService.getAttachmentsByActivity(activityId));
    }

}
