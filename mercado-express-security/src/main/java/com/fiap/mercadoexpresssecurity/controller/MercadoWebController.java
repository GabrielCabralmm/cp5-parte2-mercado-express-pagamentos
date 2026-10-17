package com.fiap.mercadoexpresssecurity.controller;

import com.fiap.mercadoexpresssecurity.model.Mercado;
import com.fiap.mercadoexpresssecurity.repository.MercadoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * CRUD completo do catálogo de produtos, exposto como páginas Web
 * (links e botões), conforme exigido no enunciado do CP5 - Parte 1.
 *
 * Leitura (listar/detalhe) é pública. Escrita (novo/salvar/editar/excluir)
 * é restrita a usuários ROLE_ADMIN (ver SecurityConfig).
 */
@Controller
@RequestMapping("/mercado")
@RequiredArgsConstructor
public class MercadoWebController {

    private final MercadoRepository mercadoRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("produtos", mercadoRepository.findAll());
        return "list";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        Mercado produto = mercadoRepository.findById(id).orElse(null);
        if (produto == null) {
            return "redirect:/mercado";
        }
        model.addAttribute("produto", produto);
        return "detalhe";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("produto", new Mercado());
        return "form";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        Mercado produto = mercadoRepository.findById(id).orElse(null);
        if (produto == null) {
            return "redirect:/mercado";
        }
        model.addAttribute("produto", produto);
        return "form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("produto") Mercado produto,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "form";
        }

        mercadoRepository.save(produto);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Produto salvo com sucesso!");
        return "redirect:/mercado";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        mercadoRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Produto excluido com sucesso!");
        return "redirect:/mercado";
    }
}
