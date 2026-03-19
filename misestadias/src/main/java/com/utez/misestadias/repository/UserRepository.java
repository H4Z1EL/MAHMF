// =============================================
// UserRepository.java
// =============================================
package com.utez.misestadias.repository;

import com.utez.misestadias.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Para Spring Security (login por email)
    Optional<User> findByEmail(String email);

    // Para verificar duplicados al registrar
    boolean existsByEmail(String email);
}
