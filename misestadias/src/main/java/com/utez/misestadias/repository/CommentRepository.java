package com.utez.misestadias.repository;

import com.utez.misestadias.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByActivity_ActivityIdOrderByCreatedAtAsc(Long activityId);

    boolean existsByActivity_ActivityId(Long activityId);
}