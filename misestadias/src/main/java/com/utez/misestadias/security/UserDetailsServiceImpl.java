package com.utez.misestadias.security;

import com.utez.misestadias.model.User;
import com.utez.misestadias.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Puente entre Spring Security y tu tabla "users" en Oracle.
 * Spring Security llamará a loadUserByUsername() automáticamente
 * durante el proceso de autenticación.
 *
 * Se guarda en: src/main/java/com/utez/misestadias/security/UserDetailsServiceImpl.java
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Busca al usuario por email en la BD
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + email
                ));

        // Verifica que el usuario esté activo (is_active = 1)
        if (user.getIsActive() == null || user.getIsActive() != 1) {
            throw new UsernameNotFoundException("Usuario inactivo: " + email);
        }

        // Construye el objeto UserDetails que Spring Security entiende.
        // El prefijo "ROLE_" es convención de Spring Security para los roles.
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                // Ejemplo: ROLE_STUDENT, ROLE_ADVISOR, ROLE_ADMIN
        );
    }
}