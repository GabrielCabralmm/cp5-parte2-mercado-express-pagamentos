package com.fiap.mercadoexpresssecurity.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Mensagem publicada na fila.pedidos.compra quando uma compra é finalizada
 * no Mercado Express. É consumida pelo projeto banco-tranquilo-pagamentos.
 *
 * Esta mesma estrutura de campos (nomes e tipos) existe também no projeto
 * banco-tranquilo-pagamentos - os dois lados usam Jackson2JsonMessageConverter,
 * então o JSON trocado precisa ser compatível entre os dois.
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
