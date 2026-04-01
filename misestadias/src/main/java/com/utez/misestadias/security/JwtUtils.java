package com.utez.misestadias.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    // Lee la clave del application.properties
    @Value("${jwt.secret}")
    private String secretString;

    @Value("${jwt.expiration}")
    private long expirationMs;

    private SecretKey key;

    // --- INICIALIZACIÓN SEGURA ---
    @PostConstruct
    public void init() {
        // Si por alguna razón el properties no carga o es muy corta, usamos una de respaldo
        // El algoritmo HS256 requiere al menos 32 caracteres (256 bits)
        if (secretString == null || secretString.length() < 32) {
            secretString = "clave_de_emergencia_mahmf_utez_2026_seguridad_total";
        }
        // Transformamos el String en una Key criptográfica real
        this.key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    // --- GENERAR TOKEN ---
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key) // Usamos la Key ya preparada
                .compact();
    }

    // --- EXTRAER DATOS ---
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // --- VALIDACIÓN ---
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token); // Si logra leerlo sin error, es válido
            return true;
        } catch (Exception e) {
            // Aquí caen tokens expirados o firmas falsas
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key) // Verificamos con nuestra Key segura
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}