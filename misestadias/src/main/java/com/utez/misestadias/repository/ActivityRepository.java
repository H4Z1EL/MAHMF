// =============================================
// ActivityRepository.java
// =============================================
package com.utez.misestadias.repository;

import com.utez.misestadias.model.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // Todas las actividades de un alumno (usa el índice idx_activity_student_status)
    List<Activity> findByStudent_UserId(Long studentId);

    // Filtradas por estado para el índice compuesto (PENDING, DELIVERED, LATE)
    Page<Activity> findByStudent_UserIdAndStatus(Long studentId, String status, Pageable pageable);
}
