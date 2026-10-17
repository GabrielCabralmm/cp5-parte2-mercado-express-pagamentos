package com.fiap.mercadoexpresssecurity.messaging;

import com.fiap.mercadoexpresssecurity.model.Compra;
import com.fiap.mercadoexpresssecurity.model.Mercado;
import com.fiap.mercadoexpresssecurity.repository.CompraRepository;
import com.fiap.mercadoexpresssecurity.repository.MercadoRepository;
import com.fiap.mercadoexpresssecurity.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Consome a fila.confirmacao.pagamento, publicada pelo microsserviço
 * banco-tranquilo-pagamentos assim que o "Banco Tranquilo" aprova ou recusa
 * o pagamento de uma compra.
 *
 * Se aprovado: atualiza o status da compra para CONFIRMADA, decrementa o
 * estoque do produto e envia o e-mail de confirmação ao comprador (logado
 * na tabela TDS_Sec_MVC_TB_EnvioEmail).
 * Se recusado: apenas atualiza o status da compra para RECUSADA.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CompraConfirmacaoListener {

    private final CompraRepository compraRepository;
    private final MercadoRepository mercadoRepository;
    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.FILA_CONFIRMACAO_PAGAMENTO)
    public void receberConfirmacao(ConfirmacaoPagamentoMessage confirmacao) {
        log.info("Confirmacao recebida para a compra #{}: aprovado={}, mensagem={}",
                confirmacao.getCompraId(), confirmacao.isAprovado(), confirmacao.getMensagem());

        Compra compra = compraRepository.findById(confirmacao.getCompraId()).orElse(null);
        if (compra == null) {
            log.warn("Compra #{} nao encontrada ao processar confirmacao de pagamento", confirmacao.getCompraId());
            return;
        }

        compra.setDataAtualizacao(LocalDateTime.now());

        if (confirmacao.isAprovado()) {
            compra.setStatus(Compra.Status.CONFIRMADA);
            compraRepository.save(compra);

            Mercado produto = compra.getProduto();
            int estoqueAtualizado = Math.max(0, produto.getEstoque() - compra.getQuantidade());
            produto.setEstoque(estoqueAtualizado);
            mercadoRepository.save(produto);

            emailService.enviarConfirmacaoCompra(compra);
        } else {
            compra.setStatus(Compra.Status.RECUSADA);
            compraRepository.save(compra);
        }
    }
}
