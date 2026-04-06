package com.utez.misestadias.security;

import com.utez.misestadias.model.User;
import com.utez.misestadias.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        if (user.getIsActive() == null || user.getIsActive() != 1) {
            throw new UsernameNotFoundException("Usuario inactivo");
        }

        // CORRECCIÓN: Aseguramos que el rol esté limpio y en mayúsculas
        String userRole = user.getRole() != null ? user.getRole().trim().toUpperCase() : "STUDENT";

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash().trim(),
                List.of(new SimpleGrantedAuthority("ROLE_" + userRole))
        );
    }
}