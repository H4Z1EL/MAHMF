package com.utez.misestadias.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long activityId;

    // FK hacia users(user_id) — representa al alumno dueño de esta actividad
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    // CLOB en Oracle → @Lob en JPA
    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    // Valores: 'PENDING', 'DELIVERED', 'LATE'
    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- Relaciones hijas (CASCADE DELETE en BD) ---

    @OneToMany(mappedBy = "activity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "activity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments;

    // --- Ciclo de vida ---

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}