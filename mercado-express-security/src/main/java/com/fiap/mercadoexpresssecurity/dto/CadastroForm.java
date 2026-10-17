package com.fiap.mercadoexpresssecurity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Formulário de cadastro (Sign Up). Não é a entidade Usuario diretamente,
 * porque aqui lidamos com senha em texto puro (antes do hash) e com o campo
 * extra "confirmarSenha", que não existe na tabela.
 */
@Data
public class CadastroForm {

    @NotBlank(message = "Informe seu nome")
    private String nome;

    @NotBlank(message = "Informe seu email")
    @Email(message = "Informe um email valido")
    private String email;

    @NotBlank(message = "Informe uma senha")
    @Size(min = 6, message = "A senha deve ter ao menos 6 caracteres")
    private String senha;

    @NotBlank(message = "Confirme sua senha")
    private String confirmarSenha;
}
