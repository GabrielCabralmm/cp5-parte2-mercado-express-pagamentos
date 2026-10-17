package com.fiap.mercadoexpresssecurity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Representa um usuário cadastrado na aplicação (tabela TDS_Users_Mercado).
 * Cada usuário possui um "role" (papel) que define seu nível de acesso:
 * - USER  -> acesso restrito básico (área do usuário, somente leitura do catálogo)
 * - ADMIN -> acesso total (painel administrativo + CRUD completo de produtos)
 */
@Entity
@Table(name = "TDS_Users_Mercado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "Email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "Senha", nullable = false, length = 255)
    private String senha;

    /** Valores esperados: "USER" ou "ADMIN". */
    @Column(name = "Role", nullable = false, length = 20)
    private String role;

    @Column(name = "DataCadastro")
    private LocalDateTime dataCadastro;

    public Usuario(String nome, String email, String senha, String role) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.role = role;
        this.dataCadastro = LocalDateTime.now();
    }
}
