package com.fiap.mercadoexpresssecurity.service;

import com.fiap.mercadoexpresssecurity.model.Compra;
import com.fiap.mercadoexpresssecurity.model.EnvioEmail;
import com.fiap.mercadoexpresssecurity.repository.EnvioEmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Envia o e-mail de confirmação de pagamento ao comprador e registra o
 * resultado do envio na tabela TDS_Sec_MVC_TB_EnvioEmail, para controle de
 * envio de e-mails (exigido no enunciado da Parte 2).
 *
 * Usa JavaMailSender via SMTP (configurado para o sandbox do Mailtrap em
 * application.yml) - o e-mail "sai de verdade" via SMTP, mas fica retido na
 * caixa de testes do Mailtrap, sem ir para a caixa real do comprador.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final EnvioEmailRepository envioEmailRepository;

    @Value("${app.mail.remetente}")
    private String remetente;

    public void enviarConfirmacaoCompra(Compra compra) {
        String assunto = "Mercado Express - Pagamento confirmado (pedido #" + compra.getId() + ")";
        String corpo = montarCorpoEmail(compra);

        EnvioEmail envioEmail = new EnvioEmail();
        envioEmail.setCompra(compra);
        envioEmail.setDestinatario(compra.getEmailComprador());
        envioEmail.setAssunto(assunto);
        envioEmail.setCorpo(corpo);
        envioEmail.setDataEnvio(LocalDateTime.now());

        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom(remetente);
            mensagem.setTo(compra.getEmailComprador());
            mensagem.setSubject(assunto);
            mensagem.setText(corpo);
            javaMailSender.send(mensagem);

            envioEmail.setStatus(EnvioEmail.Status.ENVIADO);
            log.info("E-mail de confirmacao enviado para {} (compra #{})", compra.getEmailComprador(), compra.getId());
        } catch (Exception e) {
            envioEmail.setStatus(EnvioEmail.Status.FALHA);
            log.error("Falha ao enviar e-mail de confirmacao para a compra #{}: {}", compra.getId(), e.getMessage());
        }

        envioEmailRepository.save(envioEmail);
    }

    private String montarCorpoEmail(Compra compra) {
        return "Ola, " + compra.getNomeComprador() + "!\n\n"
                + "Seu pagamento foi confirmado pelo Banco Tranquilo.\n\n"
                + "Resumo do pedido #" + compra.getId() + ":\n"
                + "- Produto: " + compra.getProduto().getNome() + "\n"
                + "- Quantidade: " + compra.getQuantidade() + "\n"
                + "- Valor total: R$ " + String.format("%.2f", compra.getValorTotal()) + "\n\n"
                + "Obrigado por comprar no Mercado Express!";
    }
}
