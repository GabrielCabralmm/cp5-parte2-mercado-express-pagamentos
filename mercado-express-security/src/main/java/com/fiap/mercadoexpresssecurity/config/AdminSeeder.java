package com.fiap.mercadoexpresssecurity.config;

import com.fiap.mercadoexpresssecurity.model.Usuario;
import com.fiap.mercadoexpresssecurity.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Garante que sempre exista, no banco, pelo menos um usuário ADMIN,
 * mesmo em um banco PostgreSQL recém-criado (ex.: ao avaliar o projeto).
 * As credenciais vêm de variáveis de ambiente, com valores padrão para
 * facilitar testes locais (ver application.yml).
 */
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.nome}")
    private String adminNome;

    @Override
    public void run(String... args) {
        if (usuarioRepository.existsByEmail(adminEmail)) {
            return;
        }

        Usuario admin = new Usuario();
        admin.setNome(adminNome);
        admin.setEmail(adminEmail);
        admin.setSenha(passwordEncoder.encode(adminPassword));
        admin.setRole("ADMIN");
        admin.setDataCadastro(LocalDateTime.now());
        usuarioRepository.save(admin);
    }
}
