package com.musicstore.bluevelvet.api.controller;

import com.musicstore.bluevelvet.domain.service.UserService;
import com.musicstore.bluevelvet.infrastructure.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("roles", User.Role.values());
        return "users/form";
    }

    @PostMapping
    public String createUser(
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String password,
            @RequestParam String role,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User user = User.builder()
                    .email(email)
                    .name(name)
                    .password(password)
                    .role(User.Role.valueOf(role))
                    .build();

            userService.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Usuário criado com sucesso!");
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar usuário: " + e.getMessage());
            return "redirect:/users/create";
        }
    }
}
