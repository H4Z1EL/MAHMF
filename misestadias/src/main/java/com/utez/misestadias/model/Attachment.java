package com.utez.misestadias.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "attachments")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long attachmentId;

    // FK hacia activities — ON DELETE CASCADE en BD
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    // Valores: 'IMAGE', 'DOCUMENT'
    @Column(name = "file_type", length = 50)
    private String fileType;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // --- Ciclo de vida ---

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}