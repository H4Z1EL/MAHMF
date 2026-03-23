package com.utez.misestadias.repository;

import com.utez.misestadias.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    Optional<StudentProfile> findByUser_UserId(Long userId);

    Optional<StudentProfile> findByMatricula(String matricula);

    boolean existsByUser_UserId(Long userId);
}