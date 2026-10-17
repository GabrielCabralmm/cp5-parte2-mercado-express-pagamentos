package com.fiap.mercadoexpresssecurity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller da página inicial pública do site.
 * A rota "/" renderiza o template "index" (index.html), que é a landing
 * page exigida pelo enunciado - a primeira tela que qualquer visitante vê,
 * com os links para Login, Cadastro e para o catálogo público.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
