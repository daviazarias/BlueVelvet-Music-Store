package com.musicstore.bluevelvet.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ⚠️ CONTROLLER TEMPORÁRIO PARA RESETAR SENHA ⚠️
 * Use apenas em desenvolvimento para resetar a senha do admin
 * DELETAR em produção!
 */
@RestController
@RequestMapping("/api/password-reset")
@RequiredArgsConstructor
@Slf4j
public class PasswordResetController {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    /**
     * Reseta a senha do usuário admin@bluevelvet.com para "123456789"
     * Acesse: GET http://localhost:8082/api/password-reset/admin
     */
    @GetMapping("/admin")
    public Map<String, String> resetAdminPassword() {
        Map<String, String> response = new HashMap<>();

        try {
            String email = "admin@bluevelvet.com";
            String newPassword = "123456789";
            String encodedPassword = passwordEncoder.encode(newPassword);

            // Atualizar senha no banco
            int updated = jdbcTemplate.update(
                    "UPDATE user SET password = ? WHERE email = ?",
                    encodedPassword,
                    email
            );

            if (updated > 0) {
                log.info("✅ Senha do admin resetada com sucesso!");
                response.put("status", "success");
                response.put("message", "Senha resetada com sucesso!");
                response.put("email", email);
                response.put("newPassword", newPassword);
                response.put("info", "Você pode fazer login agora em: http://localhost:8082/login");
            } else {
                log.warn("⚠️ Usuário admin não encontrado no banco de dados");
                response.put("status", "error");
                response.put("message", "Usuário admin não encontrado no banco de dados");
                response.put("solution", "Execute o script bluevelvet-schema.sql para criar o usuário");
            }

        } catch (Exception e) {
            log.error("❌ Erro ao resetar senha: {}", e.getMessage());
            response.put("status", "error");
            response.put("message", "Erro ao resetar senha: " + e.getMessage());
        }

        return response;
    }

    /**
     * Verifica se o usuário admin existe no banco
     * Acesse: GET http://localhost:8082/api/password-reset/check
     */
    @GetMapping("/check")
    public Map<String, Object> checkAdminUser() {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM user WHERE email = ?",
                    Integer.class,
                    "admin@bluevelvet.com"
            );

            if (count != null && count > 0) {
                response.put("status", "success");
                response.put("userExists", true);
                response.put("message", "Usuário admin existe no banco de dados");
                response.put("email", "admin@bluevelvet.com");
                response.put("expectedPassword", "123456789");
            } else {
                response.put("status", "warning");
                response.put("userExists", false);
                response.put("message", "Usuário admin NÃO existe no banco de dados");
                response.put("solution", "Execute: GET http://localhost:8082/api/password-reset/admin para criar");
            }

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erro ao verificar usuário: " + e.getMessage());
        }

        return response;
    }
}

