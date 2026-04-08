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
import java.util.Map;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final AttachmentService attachmentService;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Integer>> getActivitiesSummary(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                activityService.getSummary(userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<ActivityResponseDTO>> getActivities(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String status) {

        if (status != null) {
            return ResponseEntity.ok(
                    activityService.getActivitiesByStatus(
                            userDetails.getUsername(), status));
        }
        return ResponseEntity.ok(
                activityService.getActivitiesByStudentEmail(
                        userDetails.getUsername()));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ActivityResponseDTO>> getActivitiesByStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(
                activityService.getActivitiesByStudent(studentId));
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponseDTO> getActivityById(
            @PathVariable Long activityId) {
        return ResponseEntity.ok(
                activityService.getActivityById(activityId));
    }

    @PostMapping
    public ResponseEntity<ActivityResponseDTO> createActivity(
            @Valid @RequestBody ActivityRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(activityService.createActivity(dto));
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
        AttachmentResponseDTO response =
                attachmentService.uploadFile(activityId, file, studentEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{activityId}/attachments")
    public ResponseEntity<List<AttachmentResponseDTO>> getAttachments(
            @PathVariable Long activityId) {
        return ResponseEntity.ok(
                attachmentService.getAttachmentsByActivity(activityId));
    }
}