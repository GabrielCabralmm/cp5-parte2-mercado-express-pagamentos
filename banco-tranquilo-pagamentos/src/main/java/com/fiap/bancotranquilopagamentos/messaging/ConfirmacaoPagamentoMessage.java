package com.fiap.bancotranquilopagamentos.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Espelha com.fiap.mercadoexpresssecurity.messaging.ConfirmacaoPagamentoMessage.
 * Publicada na fila.confirmacao.pagamento com o resultado (aprovado ou
 * recusado) do processamento feito pela API de Pagamentos do Banco Tranquilo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmacaoPagamentoMessage implements Serializable {

    private Long compraId;
    private boolean aprovado;
    private String mensagem;
}
