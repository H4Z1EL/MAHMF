package com.utez.misestadias.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    // FK hacia activities — ON DELETE CASCADE está en BD; aquí CascadeType lo maneja JPA
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    // Puede ser el alumno o el asesor que escribe el comentario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // CLOB en Oracle → @Lob en JPA
    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // --- Ciclo de vida ---

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}