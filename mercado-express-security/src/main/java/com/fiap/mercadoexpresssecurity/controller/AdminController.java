package com.fiap.mercadoexpresssecurity.controller;

import com.fiap.mercadoexpresssecurity.model.Usuario;
import com.fiap.mercadoexpresssecurity.repository.MercadoRepository;
import com.fiap.mercadoexpresssecurity.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Painel administrativo (ROLE_ADMIN). É a tela para a qual um ADMIN é
 * redirecionado logo após o login - com acesso a estatísticas gerais e aos
 * atalhos de gerenciamento do catálogo (CRUD completo).
 */
@Controller
@RequiredArgsConstructor
public class AdminController {

    private final MercadoRepository mercadoRepository;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/admin")
    public String dashboard(Authentication authentication, Model model) {
        Usuario admin = usuarioRepository.findByEmail(authentication.getName()).orElse(null);

        model.addAttribute("admin", admin);
        model.addAttribute("totalProdutos", mercadoRepository.count());
        model.addAttribute("totalUsuarios", usuarioRepository.count());
        model.addAttribute("usuarios", usuarioRepository.findAll());

        return "admin";
    }
}
