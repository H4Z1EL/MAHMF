package com.utez.misestadias.repository;

import com.utez.misestadias.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // Todas las actividades de un alumno ordenadas por fecha de creación
    List<Activity> findByStudent_UserIdOrderByCreatedAtDesc(Long studentId);

    // Filtradas por estado para un alumno específico
    List<Activity> findByStudent_UserIdAndStatusOrderByCreatedAtDesc(Long studentId, String status);

    // NUEVO: Filtrar todas por estado (usado en el Service)
    List<Activity> findByStatus(String status);

    // NUEVO: Contar por estado (para la barra de progreso de Android)
    long countByStatus(String status);

    // Verificar si una actividad pertenece a un alumno
    boolean existsByActivityIdAndStudent_UserId(Long activityId, Long studentId);
}