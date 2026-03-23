package com.utez.misestadias.service;

import com.utez.misestadias.dto.CommentRequestDTO;
import com.utez.misestadias.dto.CommentResponseDTO;
import com.utez.misestadias.model.Activity;
import com.utez.misestadias.model.Comment;
import com.utez.misestadias.model.User;
import com.utez.misestadias.repository.ActivityRepository;
import com.utez.misestadias.repository.CommentRepository;
import com.utez.misestadias.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponseDTO addComment(Long activityId, CommentRequestDTO dto, String authorEmail) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Actividad no encontrada con ID: " + activityId));

        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Comment comment = new Comment();
        comment.setActivity(activity);
        comment.setAuthor(author);
        comment.setContent(dto.getContent());

        Comment saved = commentRepository.save(comment);
        return toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByActivity(Long activityId) {
        if (!activityRepository.existsById(activityId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Actividad no encontrada con ID: " + activityId);
        }
        return commentRepository.findByActivity_ActivityIdOrderByCreatedAtAsc(activityId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private CommentResponseDTO toResponseDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .authorId(comment.getAuthor().getUserId())
                .authorEmail(comment.getAuthor().getEmail())
                .authorRole(comment.getAuthor().getRole())
                .activityId(comment.getActivity().getActivityId())
                .build();
    }
}