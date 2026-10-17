package com.fiap.mercadoexpresssecurity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Corpo da requisição JSON aceita pelo endpoint REST POST /api/compras,
 * usado para testar o fluxo assíncrono de compra via Postman/Insomnia.
 */
@Data
public class CompraRequest {

    @NotNull(message = "produtoId e obrigatorio")
    private Long produtoId;

    @NotNull(message = "quantidade e obrigatoria")
    @Positive(message = "quantidade deve ser maior que zero")
    private Integer quantidade;

    @NotBlank(message = "nomeComprador e obrigatorio")
    private String nomeComprador;

    @NotBlank(message = "emailComprador e obrigatorio")
    @Email(message = "emailComprador deve ser um email valido")
    private String emailComprador;
}
