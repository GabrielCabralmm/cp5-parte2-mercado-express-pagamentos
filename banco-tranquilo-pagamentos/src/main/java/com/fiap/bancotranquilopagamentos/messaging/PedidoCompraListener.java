package com.fiap.bancotranquilopagamentos.messaging;

import com.fiap.bancotranquilopagamentos.service.IntegracaoPagamentoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Consome a fila.pedidos.compra (publicada pelo Mercado Express) e
 * representa a "Integração Pagamentos" do diagrama do enunciado: delega o
 * processamento para IntegracaoPagamentoService (que simula a chamada à API
 * de Pagamentos do Banco Tranquilo) e publica o resultado de volta na
 * fila.confirmacao.pagamento, para o Mercado Express consumir.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoCompraListener {

    private final IntegracaoPagamentoService integracaoPagamentoService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.FILA_PEDIDOS_COMPRA)
    public void receberPedidoCompra(CompraMessage pedido) {
        log.info("Pedido de compra recebido: #{} - {} x {} - comprador {}",
                pedido.getCompraId(), pedido.getQuantidade(), pedido.getProdutoNome(), pedido.getNomeComprador());

        ConfirmacaoPagamentoMessage confirmacao = integracaoPagamentoService.processarPagamento(pedido);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_CONFIRMACAO_PAGAMENTO,
                confirmacao
        );

        log.info("Confirmacao publicada na fila.confirmacao.pagamento para o pedido #{}", pedido.getCompraId());
    }
}
