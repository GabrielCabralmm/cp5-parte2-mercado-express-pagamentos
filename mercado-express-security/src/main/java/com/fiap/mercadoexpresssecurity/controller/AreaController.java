package com.fiap.mercadoexpresssecurity.controller;

import com.fiap.mercadoexpresssecurity.model.Usuario;
import com.fiap.mercadoexpresssecurity.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Área restrita de usuários comuns (ROLE_USER). É a tela para a qual um
 * usuário "comum" é redirecionado logo após o login (ver
 * RoleBasedAuthSuccessHandler) - diferente da tela de destino de um ADMIN.
 */
@Controller
@RequiredArgsConstructor
public class AreaController {

    private final UsuarioRepository usuarioRepository;

    @GetMapping("/area")
    public String area(Authentication authentication, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName()).orElse(null);
        model.addAttribute("usuario", usuario);
        return "area";
    }
}
