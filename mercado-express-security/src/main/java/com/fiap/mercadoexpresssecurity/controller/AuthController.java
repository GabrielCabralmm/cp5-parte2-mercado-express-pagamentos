package com.fiap.mercadoexpresssecurity.controller;

import com.fiap.mercadoexpresssecurity.dto.CadastroForm;
import com.fiap.mercadoexpresssecurity.model.Usuario;
import com.fiap.mercadoexpresssecurity.repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

/**
 * Controller responsável pela tela de login customizada (não é a página
 * padrão do Spring Security) e pelo fluxo de cadastro (Sign Up).
 *
 * Usuários que ainda não têm conta são direcionados para /cadastro,
 * preenchem seus dados, e passam a existir na tabela TDS_Users_Mercado
 * com role "USER" - podendo então fazer login normalmente em /login.
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Página de login própria da aplicação (template login.html).
     * O Spring Security está configurado (SecurityConfig) para usar esta
     * rota como loginPage, em vez da tela padrão gerada automaticamente.
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastroForm(Model model) {
        if (!model.containsAttribute("cadastroForm")) {
            model.addAttribute("cadastroForm", new CadastroForm());
        }
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@Valid @ModelAttribute("cadastroForm") CadastroForm form,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {

        if (!form.getSenha().equals(form.getConfirmarSenha())) {
            bindingResult.rejectValue("confirmarSenha", "senha.diferente", "As senhas nao coincidem");
        }

        if (usuarioRepository.existsByEmail(form.getEmail())) {
            bindingResult.rejectValue("email", "email.duplicado", "Ja existe uma conta com este email");
        }

        if (bindingResult.hasErrors()) {
            return "cadastro";
        }

        Usuario usuario = new Usuario();
        usuario.setNome(form.getNome());
        usuario.setEmail(form.getEmail());
        usuario.setSenha(passwordEncoder.encode(form.getSenha()));
        usuario.setRole("USER");
        usuario.setDataCadastro(LocalDateTime.now());
        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute("cadastroSucesso",
                "Conta criada com sucesso! Faca login para continuar.");
        return "redirect:/login";
    }
}
