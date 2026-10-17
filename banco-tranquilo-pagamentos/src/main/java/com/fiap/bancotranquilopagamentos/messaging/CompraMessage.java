package com.fiap.bancotranquilopagamentos.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Espelha com.fiap.mercadoexpresssecurity.messaging.CompraMessage: a mesma
 * estrutura de campos é usada dos dois lados da fila.pedidos.compra, já que
 * ambos os projetos usam Jackson2JsonMessageConverter (JSON) para serializar
 * as mensagens.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraMessage implements Serializable {

    private Long compraId;
    private String produtoNome;
    private Integer quantidade;
    private Double valorTotal;
    private String nomeComprador;
    private String emailComprador;
}
