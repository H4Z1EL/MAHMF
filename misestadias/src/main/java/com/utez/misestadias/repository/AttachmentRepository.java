package com.utez.misestadias.repository;

import com.utez.misestadias.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByActivity_ActivityId(Long activityId);
}
