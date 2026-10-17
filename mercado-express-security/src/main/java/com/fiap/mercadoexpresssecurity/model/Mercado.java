package com.fiap.mercadoexpresssecurity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade do produto do "mercado express" (tabela TDS_Sec_MVC_TB_Mercado),
 * persistida agora em PostgreSQL (CP5 - antes era Oracle, no CP4).
 */
@Entity
@Table(name = "TDS_Sec_MVC_TB_Mercado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mercado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @NotBlank(message = "O nome do produto e obrigatorio")
    @Column(name = "Nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "Tipo", length = 60)
    private String tipo;

    @Column(name = "Setor", length = 60)
    private String setor;

    @Column(name = "Tamanho", length = 30)
    private String tamanho;

    @NotNull(message = "O preco e obrigatorio")
    @Positive(message = "O preco deve ser maior que zero")
    @Column(name = "Preco", nullable = false)
    private Double preco;

    /** Custo de aquisicao/producao do item, usado para calcular o lucro no dashboard (CP5 Parte 2). */
    @Column(name = "Custo")
    private Double custo;

    /** Quantidade disponivel em estoque, decrementada a cada compra confirmada (CP5 Parte 2). */
    @NotNull(message = "O estoque e obrigatorio")
    @Column(name = "Estoque", nullable = false)
    private Integer estoque = 0;
}
