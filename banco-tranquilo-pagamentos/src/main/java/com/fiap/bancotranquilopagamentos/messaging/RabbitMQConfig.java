package com.fiap.bancotranquilopagamentos.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração RabbitMQ deste microsserviço (Integração de Pagamentos do
 * Banco Tranquilo). Consome a fila.pedidos.compra (publicada pelo projeto
 * mercado-express-security) e publica o resultado em fila.confirmacao.pagamento.
 *
 * IMPORTANTE: os nomes de exchange/fila/routing key abaixo precisam ser
 * EXATAMENTE os mesmos usados no projeto mercado-express-security
 * (com.fiap.mercadoexpresssecurity.messaging.RabbitMQConfig) - os dois
 * projetos compartilham o mesmo broker RabbitMQ, então uma divergência de
 * nome quebra a comunicação entre os dois lados.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "mercado.express.exchange";

    public static final String FILA_PEDIDOS_COMPRA = "fila.pedidos.compra";
    public static final String ROUTING_KEY_PEDIDO_COMPRA = "pedido.compra";

    public static final String FILA_CONFIRMACAO_PAGAMENTO = "fila.confirmacao.pagamento";
    public static final String ROUTING_KEY_CONFIRMACAO_PAGAMENTO = "confirmacao.pagamento";

    @Bean
    public DirectExchange mercadoExpressExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue filaPedidosCompra() {
        return new Queue(FILA_PEDIDOS_COMPRA, true);
    }

    @Bean
    public Queue filaConfirmacaoPagamento() {
        return new Queue(FILA_CONFIRMACAO_PAGAMENTO, true);
    }

    @Bean
    public Binding bindingPedidosCompra(Queue filaPedidosCompra, DirectExchange mercadoExpressExchange) {
        return BindingBuilder.bind(filaPedidosCompra).to(mercadoExpressExchange).with(ROUTING_KEY_PEDIDO_COMPRA);
    }

    @Bean
    public Binding bindingConfirmacaoPagamento(Queue filaConfirmacaoPagamento, DirectExchange mercadoExpressExchange) {
        return BindingBuilder.bind(filaConfirmacaoPagamento).to(mercadoExpressExchange).with(ROUTING_KEY_CONFIRMACAO_PAGAMENTO);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        // Ver comentario equivalente no mercado-express-security: usamos
        // INFERRED porque os dois projetos tem pacotes Java diferentes, e o
        // valor padrao (TYPE_ID) tentaria usar o nome da classe de quem
        // enviou a mensagem, que nao existe neste projeto.
        converter.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
