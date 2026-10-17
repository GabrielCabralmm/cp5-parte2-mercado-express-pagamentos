package com.fiap.mercadoexpresssecurity.exception;

import com.fiap.mercadoexpresssecurity.controller.CompraApiController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Tratamento de erro apenas para o CompraApiController (API REST de testes
 * via Postman), devolvendo 404 com uma mensagem clara em vez do 500 padrão
 * quando o produtoId ou a compra informados não existem.
 */
@RestControllerAdvice(assignableTypes = CompraApiController.class)
public class CompraApiExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> tratarNaoEncontrado(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", ex.getMessage()));
    }
}
