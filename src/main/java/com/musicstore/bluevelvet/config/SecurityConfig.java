package com.musicstore.bluevelvet.config;

import com.musicstore.bluevelvet.domain.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // ← MANTÉM: Desabilita CSRF
                .authorizeHttpRequests(auth -> auth
                        // ← SWAGGER: Mantém permitido
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // ← NOVO: Rotas de autenticação públicas
                        .requestMatchers("/login", "/register", "/css/**", "/js/**", "/images/**", "/uploads/**", "/shop/**").permitAll()
                        // ← NOVO: Dashboard requer autenticação
                        .requestMatchers("/dashboard/**", "/admin/**").authenticated()
                        .anyRequest().authenticated()
                )
                // ← NOVO: Form login ao invés de Basic Auth
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                )
                // ← NOVO: Remember Me (Lembrar-se de mim)
                .rememberMe(remember -> remember
                        .key("bluevelvetSecretKey2024")  // Chave secreta para encriptação
                        .tokenValiditySeconds(86400 * 1)  // 7 dias
                        .rememberMeParameter("remember-me")  // Nome do parâmetro do checkbox
                        .rememberMeCookieName("bluevelvet-remember-me")  // Nome do cookie
                        .userDetailsService(userDetailsService)  // Serviço de detalhes do usuário
                )
                // ← NOVO: Logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "bluevelvet-remember-me")  // Remove também o cookie remember-me
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/swagger-ui/**", "/v3/api-docs"
        );
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
