package com.fiap.mercadoexpresssecurity.controller;

import com.fiap.mercadoexpresssecurity.dto.CompraRequest;
import com.fiap.mercadoexpresssecurity.model.Compra;
import com.fiap.mercadoexpresssecurity.repository.CompraRepository;
import com.fiap.mercadoexpresssecurity.service.CompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

/**
 * API REST para testar o fluxo assíncrono de compra do CP5 - Parte 2 via
 * Postman/Insomnia (ver enunciado: "um exemplo de requisição de compra em
 * JSON para usar no Postman"). Endpoint público (ver SecurityConfig), sem
 * necessidade de login ou token - o foco aqui é demonstrar a mensageria.
 *
 * Fluxo de teste sugerido:
 * 1) POST /api/compras com o JSON de exemplo -> retorna 201 com status PENDENTE
 * 2) Aguardar alguns segundos (processamento assincrono via RabbitMQ)
 * 3) GET /api/compras/{id} -> status deve ter mudado para CONFIRMADA ou RECUSADA
 */
@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraApiController {

    private final CompraService compraService;
    private final CompraRepository compraRepository;

    @PostMapping
    public ResponseEntity<Compra> criar(@Valid @RequestBody CompraRequest request) {
        Compra compra = compraService.finalizarCompra(
                request.getProdutoId(),
                request.getQuantidade(),
                request.getNomeComprador(),
                request.getEmailComprador()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(compra);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Compra> buscarPorId(@PathVariable Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Compra nao encontrada: " + id));
        return ResponseEntity.ok(compra);
    }

    @GetMapping
    public ResponseEntity<Iterable<Compra>> listar() {
        return ResponseEntity.ok(compraRepository.findAllByOrderByDataCompraDesc());
    }
}
