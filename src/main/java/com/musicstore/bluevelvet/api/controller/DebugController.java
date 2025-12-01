package com.musicstore.bluevelvet.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * ⚠️ CONTROLLER TEMPORÁRIO DE DEBUG - REMOVER EM PRODUÇÃO ⚠️
 * Este endpoint expõe credenciais sensíveis e deve ser usado apenas em desenvolvimento.
 */
@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Value("${app.admin.username:admin@bluevelvet.com}")
    private String adminUsername;

    @Value("${app.admin.password:123456789}")
    private String adminPassword;

    /**
     * Endpoint temporário para visualizar as credenciais do admin.
     * ⚠️ DELETAR ESTE ENDPOINT APÓS DESCOBRIR A SENHA ⚠️
     * <p>
     * Acesse: GET http://localhost:8080/api/debug/admin-credentials
     */
    @GetMapping("/admin-credentials")
    public ResponseEntity<Map<String, String>> getAdminCredentials() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", adminUsername);
        credentials.put("password", adminPassword);
        credentials.put("warning", "⚠️ ESTE ENDPOINT DEVE SER REMOVIDO EM PRODUÇÃO!");
        credentials.put("info", "Use estas credenciais para fazer login no sistema");

        return ResponseEntity.ok(credentials);
    }

    /**
     * Endpoint adicional para verificar informações do sistema
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getSystemInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("status", "Debug mode ativo");
        info.put("endpoint", "GET /api/debug/admin-credentials");
        info.put("description", "Use o endpoint acima para visualizar as credenciais do admin");

        return ResponseEntity.ok(info);
    }
}

