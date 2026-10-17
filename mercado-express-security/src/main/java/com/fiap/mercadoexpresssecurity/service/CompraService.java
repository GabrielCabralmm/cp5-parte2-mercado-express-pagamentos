package com.fiap.mercadoexpresssecurity.service;

import com.fiap.mercadoexpresssecurity.messaging.CompraMessage;
import com.fiap.mercadoexpresssecurity.messaging.RabbitMQConfig;
import com.fiap.mercadoexpresssecurity.model.Compra;
import com.fiap.mercadoexpresssecurity.model.Mercado;
import com.fiap.mercadoexpresssecurity.repository.CompraRepository;
import com.fiap.mercadoexpresssecurity.repository.MercadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

/**
 * Orquestra a finalização de uma compra: grava o pedido no banco com status
 * PENDENTE e publica a mensagem correspondente na fila.pedidos.compra, para
 * processamento assíncrono pelo microsserviço banco-tranquilo-pagamentos.
 *
 * É chamado tanto pelo fluxo de carrinho (tela Web) quanto pelo endpoint
 * REST /api/compras (usado para testes via Postman/Insomnia).
 */
@Service
@RequiredArgsConstructor
public class CompraService {

    private final MercadoRepository mercadoRepository;
    private final CompraRepository compraRepository;
    private final RabbitTemplate rabbitTemplate;

    public Compra finalizarCompra(Long produtoId, Integer quantidade, String nomeComprador, String emailComprador) {
        Mercado produto = mercadoRepository.findById(produtoId)
                .orElseThrow(() -> new NoSuchElementException("Produto nao encontrado: " + produtoId));

        double valorTotal = produto.getPreco() * quantidade;

        Compra compra = new Compra();
        compra.setProduto(produto);
        compra.setQuantidade(quantidade);
        compra.setValorTotal(valorTotal);
        compra.setNomeComprador(nomeComprador);
        compra.setEmailComprador(emailComprador);
        compra.setStatus(Compra.Status.PENDENTE);
        compra.setDataCompra(LocalDateTime.now());
        compra = compraRepository.save(compra);

        CompraMessage mensagem = new CompraMessage(
                compra.getId(),
                produto.getNome(),
                quantidade,
                valorTotal,
                nomeComprador,
                emailComprador
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_PEDIDO_COMPRA,
                mensagem
        );

        return compra;
    }
}
