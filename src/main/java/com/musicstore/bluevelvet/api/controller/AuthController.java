package com.musicstore.bluevelvet.api.controller;

import com.musicstore.bluevelvet.domain.service.UserService;
import com.musicstore.bluevelvet.infrastructure.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    public static final String REDIRECT_REGISTER = "redirect:/register";
    public static final String ERROR_MESSAGE = "errorMessage";
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // ============== LOGIN ==============

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute(ERROR_MESSAGE, "E-mail ou senha incorretos. Tente novamente.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "Você foi desconectado com sucesso.");
        }
        return "login";
    }

    // ============== REGISTER ==============

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", User.Role.values());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user,
                           RedirectAttributes redirectAttributes) {
        try {
            // Valida se já existe usuário com esse email
            if (userService.findByEmail(user.getEmail()).isPresent()) {
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE,
                        "E-mail já cadastrado no sistema.");
                return REDIRECT_REGISTER;
            }

            // Valida comprimento da senha
            if (user.getPassword().length() < 8) {
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE,
                        "A senha deve ter no mínimo 8 caracteres.");
                return REDIRECT_REGISTER;
            }

            // Apenas ADMIN pode registrar novos usuários
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getAuthorities().stream()
                    .noneMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATOR"))) {
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE,
                        "Apenas administradores podem registrar novos usuários.");
                return REDIRECT_REGISTER;
            }

            // Salva o novo usuário
            userService.save(user);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Usuário registrado com sucesso! Agora faça login.");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE,
                    "Erro ao registrar usuário: " + e.getMessage());
            return REDIRECT_REGISTER;
        }
    }

}
