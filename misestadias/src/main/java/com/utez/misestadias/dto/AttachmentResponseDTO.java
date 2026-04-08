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
public class AttachmentResponseDTO {
    private Long attachmentId;
    private Long activityId;
    private String fileUrl;
    private String fileType;
    private String fileName;
    private LocalDateTime createdAt;
}
