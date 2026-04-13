package com.utez.misestadias.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityResponseDTO {

    private Long activityId;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long studentId;
    private String studentName;
    private String studentEmail;
}