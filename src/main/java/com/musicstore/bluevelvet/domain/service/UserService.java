package com.musicstore.bluevelvet.domain.service;

import com.musicstore.bluevelvet.infrastructure.entity.User;
import com.musicstore.bluevelvet.infrastructure.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Salva um novo usuário com senha criptografada
     */
    public User save(User user) {
        // Criptografa a senha antes de salvar
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Busca usuário pelo email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Busca usuário pelo ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Atualiza um usuário existente
     */
    public User update(User user) {
        return userRepository.save(user);
    }

    /**
     * Deleta um usuário
     */
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Lista todos os usuários
     */
    public java.util.List<User> findAll() {
        return userRepository.findAll();
    }
}
