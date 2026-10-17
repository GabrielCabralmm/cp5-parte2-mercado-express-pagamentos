package com.fiap.bancotranquilopagamentos.service;

import com.fiap.bancotranquilopagamentos.messaging.CompraMessage;
import com.fiap.bancotranquilopagamentos.messaging.ConfirmacaoPagamentoMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Simula a "API Plataforma de Pagamentos" conversando com o Banco Tranquilo
 * (ver diagrama do enunciado). Como o Banco Tranquilo é fictício, não existe
 * uma API real para chamar - este serviço representa essa chamada:
 * - Adiciona um pequeno atraso artificial (processamento assíncrono real
 *   levaria um tempo, e isso também deixa visível no vídeo/demo que a
 *   confirmação não é instantânea).
 * - Aprova a grande maioria das compras (90%), e recusa aleatoriamente o
 *   restante (10%), só para demonstrar os dois caminhos possíveis do fluxo.
 */
@Service
@Slf4j
public class IntegracaoPagamentoService {

    private static final double TAXA_APROVACAO = 0.9;

    public ConfirmacaoPagamentoMessage processarPagamento(CompraMessage pedido) {
        log.info("Enviando pedido de compra #{} para a API de Pagamentos do Banco Tranquilo...", pedido.getCompraId());

        simularLatenciaDeRede();

        boolean aprovado = ThreadLocalRandom.current().nextDouble() < TAXA_APROVACAO;
        String mensagem = aprovado
                ? "Pagamento aprovado pelo Banco Tranquilo."
                : "Pagamento recusado pelo Banco Tranquilo (saldo insuficiente ou transacao nao autorizada).";

        log.info("Resultado do pedido #{}: {}", pedido.getCompraId(), mensagem);

        return new ConfirmacaoPagamentoMessage(pedido.getCompraId(), aprovado, mensagem);
    }

    private void simularLatenciaDeRede() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(800, 2500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
