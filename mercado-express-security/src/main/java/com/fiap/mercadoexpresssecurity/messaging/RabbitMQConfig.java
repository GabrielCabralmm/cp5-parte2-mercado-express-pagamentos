package com.fiap.mercadoexpresssecurity.messaging;

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
 * Configuração do RabbitMQ para o fluxo assíncrono de compras do CP5 - Parte 2.
 *
 * Fluxo (ver diagrama do enunciado):
 * Mercado Express (este projeto) publica um pedido de compra na fila
 * FILA_PEDIDOS_COMPRA. O microsserviço separado "banco-tranquilo-pagamentos"
 * consome essa fila, simula a integração com a API de Pagamentos do Banco
 * Tranquilo, e publica o resultado na fila FILA_CONFIRMACAO_PAGAMENTO, que
 * este projeto consome (ver CompraConfirmacaoListener) para enviar o e-mail
 * de confirmação e atualizar o status da compra.
 *
 * IMPORTANTE: os nomes de exchange/fila/routing key abaixo são EXATAMENTE os
 * mesmos usados no projeto banco-tranquilo-pagamentos - não altere um lado
 * sem replicar a mudança no outro.
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
        // IMPORTANTE: este projeto e o banco-tranquilo-pagamentos sao duas
        // aplicacoes Java separadas, com pacotes diferentes. Por padrao, o
        // conversor tenta montar o objeto usando o nome completo da classe
        // de quem ENVIOU a mensagem (via header __TypeId__), o que falharia
        // aqui porque essa classe nao existe do outro lado. Usando
        // INFERRED, ele usa o tipo do parametro do metodo @RabbitListener
        // que vai receber a mensagem, o que funciona entre projetos distintos.
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
