package com.fiap.mercadoexpresssecurity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Representa um pedido de compra feito no Mercado Express (tabela
 * TDS_Sec_MVC_TB_Compra). É criado com status PENDENTE assim que o pedido é
 * publicado na fila RabbitMQ (fila.pedidos.compra), e tem seu status
 * atualizado de forma assíncrona quando a confirmação chega de volta pela
 * fila.confirmacao.pagamento (ver CompraConfirmacaoListener).
 */
@Entity
@Table(name = "TDS_Sec_MVC_TB_Compra")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Compra {

    public enum Status {
        PENDENTE, CONFIRMADA, RECUSADA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ProdutoId", nullable = false)
    private Mercado produto;

    @Column(name = "Quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "ValorTotal", nullable = false)
    private Double valorTotal;

    @Column(name = "NomeComprador", nullable = false, length = 120)
    private String nomeComprador;

    @Column(name = "EmailComprador", nullable = false, length = 150)
    private String emailComprador;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false, length = 20)
    private Status status;

    @Column(name = "DataCompra", nullable = false)
    private LocalDateTime dataCompra;

    @Column(name = "DataAtualizacao")
    private LocalDateTime dataAtualizacao;
}
