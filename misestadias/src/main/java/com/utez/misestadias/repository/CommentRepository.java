// =============================================
// CommentRepository.java
// =============================================
package com.utez.misestadias.repository;

import com.utez.misestadias.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Todos los comentarios de una actividad (usa el índice idx_comments_activity)
    List<Comment> findByActivity_ActivityId(Long activityId);
}
