package com.utez.misestadias.controller;

import com.utez.misestadias.dto.CommentRequestDTO;
import com.utez.misestadias.dto.CommentResponseDTO;
import com.utez.misestadias.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities/{activityId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDTO> addComment(
            @PathVariable Long activityId,
            @Valid @RequestBody CommentRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String authorEmail = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(activityId, dto, authorEmail));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable Long activityId) {
        return ResponseEntity.ok(commentService.getCommentsByActivity(activityId));
    }
}