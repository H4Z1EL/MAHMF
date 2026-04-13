package com.utez.misestadias.repository;

import com.utez.misestadias.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByStudent_UserIdOrderByCreatedAtDesc(Long studentId);

    List<Activity> findByStudent_UserIdAndStatusOrderByCreatedAtDesc(Long studentId, String status);

    List<Activity> findByStatus(String status);

    long countByStatus(String status);

    boolean existsByActivityIdAndStudent_UserId(Long activityId, Long studentId);
}