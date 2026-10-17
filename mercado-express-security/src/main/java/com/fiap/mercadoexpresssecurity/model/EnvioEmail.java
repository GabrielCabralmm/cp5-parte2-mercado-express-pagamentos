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
 * Log de envio de e-mail de confirmação de pagamento (tabela
 * TDS_Sec_MVC_TB_EnvioEmail), exigido no enunciado da Parte 2 para controle
 * de envio de e-mails aos clientes.
 */
@Entity
@Table(name = "TDS_Sec_MVC_TB_EnvioEmail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnvioEmail {

    public enum Status {
        ENVIADO, FALHA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CompraId", nullable = false)
    private Compra compra;

    @Column(name = "Destinatario", nullable = false, length = 150)
    private String destinatario;

    @Column(name = "Assunto", nullable = false, length = 200)
    private String assunto;

    @Column(name = "Corpo", length = 2000)
    private String corpo;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false, length = 20)
    private Status status;

    @Column(name = "DataEnvio", nullable = false)
    private LocalDateTime dataEnvio;
}
