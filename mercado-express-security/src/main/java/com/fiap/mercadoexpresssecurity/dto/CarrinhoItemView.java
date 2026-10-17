package com.fiap.mercadoexpresssecurity.dto;

import com.fiap.mercadoexpresssecurity.model.Mercado;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Representa uma linha do carrinho de compras na tela (produto + quantidade
 * + subtotal calculado). Usado apenas para exibição, nunca é persistido.
 */
@Data
@AllArgsConstructor
public class CarrinhoItemView {
    private Mercado produto;
    private Integer quantidade;
    private Double subtotal;
}
