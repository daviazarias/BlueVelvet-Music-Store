package com.musicstore.bluevelvet.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * ⚠️ RESET DE SENHA DO ADMIN - USE APENAS QUANDO NECESSÁRIO ⚠️
 * <p>
 * Este componente reseta a senha do admin para "admin123" na inicialização.
 * <p>
 * PARA USAR:
 * 1. Descomente @Component acima
 * 2. Reinicie a aplicação
 * 3. Tente fazer login com: admin@bluevelvet.com / admin123
 * 4. Comente @Component novamente após o reset
 * <p>
 * CREDENCIAIS APÓS O RESET:
 * - Email: admin@bluevelvet.com
 * - Senha: admin123
 */
@Component // ← RESET ATIVADO! Comente esta linha após fazer login
@Slf4j
public class AdminPasswordReset implements ApplicationRunner {

    private final JdbcTemplate jdbc;
    private final PasswordEncoder passwordEncoder;

    public AdminPasswordReset(JdbcTemplate jdbc, PasswordEncoder passwordEncoder) {
        this.jdbc = jdbc;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String newPassword = "admin123"; // ← ALTERE A SENHA AQUI SE DESEJAR
        String adminEmail = "admin@bluevelvet.com";

        // Verificar se o usuário existe
        Integer exists = jdbc.queryForObject(
                "SELECT COUNT(*) FROM `user` WHERE email = ?",
                Integer.class,
                adminEmail
        );

        if (exists != null && exists > 0) {
            // Usuário existe - atualizar senha
            String encodedPassword = passwordEncoder.encode(newPassword);

            int updated = jdbc.update(
                    "UPDATE `user` SET password = ? WHERE email = ?",
                    encodedPassword,
                    adminEmail
            );

            if (updated > 0) {
                log.warn("╔════════════════════════════════════════════════╗");
                log.warn("║  ⚠️  SENHA DO ADMIN RESETADA COM SUCESSO! ⚠️   ║");
                log.warn("╠════════════════════════════════════════════════╣");
                log.warn("║  Email: {}              ║", adminEmail);
                log.warn("║  Senha: {}                       ║", newPassword);
                log.warn("╠════════════════════════════════════════════════╣");
                log.warn("║  ⚠️  DESATIVE ESTE COMPONENTE APÓS O LOGIN! ⚠️  ║");
                log.warn("║  Comente @Component em AdminPasswordReset.java ║");
                log.warn("╚════════════════════════════════════════════════╝");
            }
        } else {
            // Usuário não existe - criar novo
            String encodedPassword = passwordEncoder.encode(newPassword);

            int inserted = jdbc.update(
                    "INSERT INTO `user` (email, name, password, role) VALUES (?, ?, ?, ?)",
                    adminEmail,
                    "Administrador",
                    encodedPassword,
                    "ADMINISTRATOR"
            );

            if (inserted > 0) {
                log.warn("╔════════════════════════════════════════════════╗");
                log.warn("║  ✓ NOVO USUÁRIO ADMIN CRIADO COM SUCESSO!     ║");
                log.warn("╠════════════════════════════════════════════════╣");
                log.warn("║  Email: {}              ║", adminEmail);
                log.warn("║  Senha: {}                       ║", newPassword);
                log.warn("╠════════════════════════════════════════════════╣");
                log.warn("║  ⚠️  DESATIVE ESTE COMPONENTE APÓS O LOGIN! ⚠️  ║");
                log.warn("║  Comente @Component em AdminPasswordReset.java ║");
                log.warn("╚════════════════════════════════════════════════╝");
            }
        }
    }
}

