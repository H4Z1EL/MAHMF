package com.utez.misestadias.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    // Lee los valores del application.properties
    @Value("${jwt.secret}")
    private String secretString;

    @Value("${jwt.expiration}")
    private long expirationMs;

    // -------------------------------------------------------------------
    // Construye la clave criptográfica a partir del String del properties
    // -------------------------------------------------------------------
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    // -------------------------------------------------------------------
    // GENERAR TOKEN
    // Incluye el email como "subject" y el rol como un claim adicional.
    // La app móvil y el panel web leerán el rol para saber qué mostrar.
    // -------------------------------------------------------------------
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email)                             // Quién es el usuario
                .claim("role", role)                        // Claim extra: su rol
                .issuedAt(new Date())                       // Cuándo fue emitido
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())                  // Firma con HMAC-SHA256
                .compact();
    }

    // -------------------------------------------------------------------
    // EXTRAER USERNAME (email)
    // Lo usará JwtAuthFilter para identificar al usuario en cada request.
    // -------------------------------------------------------------------
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // -------------------------------------------------------------------
    // EXTRAER ROL
    // Útil para lógica de autorización en los servicios.
    // -------------------------------------------------------------------
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token); // Si esto no lanza excepción, el token es válido
            return true;
        } catch (Exception e) {
            // io.jsonwebtoken.security.SignatureException  → firma inválida
            // io.jsonwebtoken.ExpiredJwtException          → token expirado
            // io.jsonwebtoken.MalformedJwtException        → formato incorrecto
            return false;
        }
    }

    // -------------------------------------------------------------------
    // MÉTODO PRIVADO: extrae todos los claims del token
    // -------------------------------------------------------------------
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}