// java
package com.musicstore.bluevelvet.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.Statement;

// @Component // ← Desabilitado: o admin já existe no schema SQL (bluevelvet-schema.sql)
// Se quiser que o sistema crie automaticamente um admin na inicialização, descomente @Component
@Slf4j
public class AdminDataInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbc;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin@bluevelvet.com}")
    private String adminUsername;

    @Value("${app.admin.password:123456789}") // ← Senha padrão: 123456789
    private String adminPassword;

    public AdminDataInitializer(JdbcTemplate jdbc, PasswordEncoder passwordEncoder) {
        this.jdbc = jdbc;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // use backticks around `user` because user can be a reserved identifier
        Integer exists = jdbc.queryForObject(
                "select count(*) from `user` where email = ?",
                Integer.class,
                adminUsername
        );

        if (exists == null) {
            // defensive: treat as no users found
            exists = 0;
        }

        if (exists == 0) {
            String encoded = passwordEncoder.encode(adminPassword);

            KeyHolder userKey = new GeneratedKeyHolder();
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "insert into `user`(email, password, role) values(?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, adminUsername);
                ps.setString(2, encoded);
                ps.setString(3, "ADMINISTRATOR");
                return ps;
            }, userKey);

            log.info("Admin inicial criado: {}", adminUsername);
        }
    }
}
