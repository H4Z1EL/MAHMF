package com.utez.misestadias.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    public void sendRecoveryCode(String email, String code) {
        log.info("=================================================");
        log.info("  SIMULACIÓN DE CORREO DE RECUPERACIÓN");
        log.info("  Para:    {}", email);
        log.info("  Código:  {}", code);
        log.info("  Expira:  en 10 minutos");
        log.info("=================================================");
    }
}