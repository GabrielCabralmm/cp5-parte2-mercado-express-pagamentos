package com.fiap.mercadoexpresssecurity.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Mensagem publicada na fila.confirmacao.pagamento pelo microsserviço
 * banco-tranquilo-pagamentos, informando se o pagamento de uma compra foi
 * aprovado ou recusado pelo "Banco Tranquilo". Consumida aqui no Mercado
 * Express por CompraConfirmacaoListener.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmacaoPagamentoMessage implements Serializable {

    private Long compraId;
    private boolean aprovado;
    private String mensagem;
}
