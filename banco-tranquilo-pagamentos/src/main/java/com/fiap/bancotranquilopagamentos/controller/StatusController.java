package com.fiap.bancotranquilopagamentos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint simples só para confirmar que o microsserviço está no ar
 * (útil ao gravar o vídeo de demonstração).
 */
@RestController
public class StatusController {

    @GetMapping("/")
    public Map<String, String> status() {
        return Map.of(
                "servico", "banco-tranquilo-pagamentos",
                "status", "UP",
                "descricao", "Integracao de Pagamentos do Banco Tranquilo - consumidor RabbitMQ"
        );
    }
}
