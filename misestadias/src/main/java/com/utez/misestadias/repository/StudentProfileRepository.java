package com.utez.misestadias.repository;

import com.utez.misestadias.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    // Buscar el perfil a partir del ID de usuario
    Optional<StudentProfile> findByUser_UserId(Long userId);

    // Buscar por matrícula (campo único)
    Optional<StudentProfile> findByMatricula(String matricula);
}
