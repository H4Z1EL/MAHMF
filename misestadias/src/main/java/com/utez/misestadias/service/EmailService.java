package com.utez.misestadias.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private static final String FROM = "mahmf.verificacion@gmail.com";

    public void sendRecoveryCode(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM);
            message.setTo(toEmail);
            message.setSubject("Mis Estadías UTEZ — Código de recuperación");
            message.setText(
                    "Hola,\n\n" +
                            "Recibimos una solicitud para restablecer tu contraseña en el sistema Mis Estadías UTEZ.\n\n" +
                            "Tu código de verificación es:\n\n" +
                            "    " + code + "\n\n" +
                            "Este código expira en 10 minutos.\n\n" +
                            "Si no solicitaste este cambio, ignora este mensaje.\n\n" +
                            "— Sistema Mis Estadías, UTEZ"
            );
            mailSender.send(message);
            log.info("Correo de recuperación enviado a: {}", toEmail);
        } catch (Exception e) {
            log.error("Error al enviar correo a {}: {}", toEmail, e.getMessage());
        }
    }
}
